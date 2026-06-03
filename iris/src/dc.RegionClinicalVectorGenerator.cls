Class dc.RegionClinicalVectorGenerator
{

ClassMethod run() [ Language = python ]
{
    import iris
    import math
    import hashlib
    from collections import defaultdict, Counter

    VECTOR_DIMENSION = 384
    VECTOR_VERSION = "clinical-region-v3-hash-384"

    def feature_index(feature):
        digest = hashlib.md5(feature.encode("utf-8")).hexdigest()
        return int(digest, 16) % VECTOR_DIMENSION

    def clean(value):
        return "" if value is None else str(value).strip()

    sql = """
    SELECT
        l.addressCity,
        l.addressState,
        GetProp(GetJSON(ro.ResourceString,'code'),'text') AS ObservationName,
        o.code AS ObservationCode,
        c.code AS ConditionCode,
        GetProp(GetJSON(rd.ResourceString,'code'),'text') AS DiagnosticReportName,
        COUNT(*) AS Occurrences
    FROM HSFHIR_X0001_S.OBSERVATION o
    INNER JOIN HSFHIR_X0001_S.ENCOUNTER e
        ON o.encounter = 'Encounter/' || e._id
    INNER JOIN HSFHIR_X0001_S.LOCATION l
        ON e.location LIKE '%' || l._id || '%'
    LEFT JOIN HSFHIR_X0001_S.CONDITION c
        ON c.patient = o.patient
    LEFT JOIN HSFHIR_X0001_S.DIAGNOSTICREPORT dr
        ON dr.patient = o.patient
    INNER JOIN HSFHIR_X0001_R.Rsrc ro
        ON ro.ResourceType = 'Observation'
        AND ro.ResourceId = o._id
    LEFT JOIN HSFHIR_X0001_R.Rsrc rd
        ON rd.ResourceType = 'DiagnosticReport'
        AND rd.ResourceId = dr._id
    WHERE
        o.dateStart >= DATEADD('day', -30, CURRENT_DATE)
        AND l.addressCity IS NOT NULL
        AND l.addressState IS NOT NULL
    GROUP BY
        l.addressCity,
        l.addressState,
        GetProp(GetJSON(ro.ResourceString,'code'),'text'),
        o.code,
        c.code,
        GetProp(GetJSON(rd.ResourceString,'code'),'text')
    ORDER BY
        Occurrences DESC
    """

    rs = iris.sql.exec(sql)

    region_features = defaultdict(lambda: defaultdict(float))
    region_diseases = defaultdict(Counter)
    region_disease_codes = defaultdict(Counter)
    region_symptoms = defaultdict(Counter)

    for row in rs:
        city = clean(row[0]) or "UNKNOWN_CITY"
        state = clean(row[1]) or "UNKNOWN_STATE"

        observation_name = clean(row[2])
        observation_code = clean(row[3])
        condition_code = clean(row[4])
        diagnostic_name = clean(row[5])
        occurrences = float(row[6] or 0)

        region_key = city + "|" + state

        features = []

        if observation_name:
            feature = "OBS_NAME:" + observation_name.lower()
            features.append(feature)
            region_symptoms[region_key][observation_name] += occurrences

        if observation_code:
            feature = "OBS_CODE:" + observation_code.lower()
            features.append(feature)

        if condition_code:
            feature = "COND_CODE:" + condition_code.lower()
            features.append(feature)
            region_disease_codes[region_key][condition_code] += occurrences
            region_diseases[region_key][condition_code] += occurrences

        if diagnostic_name:
            feature = "REPORT_NAME:" + diagnostic_name.lower()
            features.append(feature)
            region_diseases[region_key][diagnostic_name] += occurrences

        for feature in features:
            region_features[region_key][feature] += occurrences

    insert_stmt = iris.sql.prepare("""
        INSERT INTO dc.RegionClinicalVector
        (
            AddressCity,
            AddressState,
            VectorVersion,
            ClinicalVector,
            FeatureCount,
            TotalOccurrences,
            IdentifiedDiseases,
            IdentifiedDiseaseCodes,
            MainSymptoms,
            TopFeatures,
            ClinicalSummary
        )
        VALUES (?, ?, ?, TO_VECTOR(?, DOUBLE), ?, ?, ?, ?, ?, ?, ?)
    """)

    for region_key, features_map in region_features.items():
        city, state = region_key.split("|", 1)

        vector = [0.0] * VECTOR_DIMENSION
        total_occurrences = 0.0

        for feature, value in features_map.items():
            index = feature_index(feature)
            vector[index] += value
            total_occurrences += value

        norm = math.sqrt(sum(v * v for v in vector))

        if norm > 0:
            vector = [v / norm for v in vector]

        vector_str = "[" + ",".join(str(v) for v in vector) + "]"

        diseases = [k for k, _ in region_diseases[region_key].most_common(10)]
        disease_codes = [k for k, _ in region_disease_codes[region_key].most_common(10)]
        symptoms = [k for k, _ in region_symptoms[region_key].most_common(10)]
        top_features = [k for k, _ in Counter(features_map).most_common(10)]

        clinical_summary = (
            "Region " + city + "-" + state +
            " has " + str(int(total_occurrences)) +
            " clinical occurrences. Main diseases/codes: " +
            ", ".join(diseases[:5]) +
            ". Main symptoms/findings: " +
            ", ".join(symptoms[:5]) + "."
        )

        insert_stmt.execute(
            city,
            state,
            VECTOR_VERSION,
            vector_str,
            len(features_map),
            total_occurrences,
            ", ".join(diseases),
            ", ".join(disease_codes),
            ", ".join(symptoms),
            " | ".join(top_features),
            clinical_summary
        )
}

}