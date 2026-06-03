Class dc.RegionClinicalVectorGenerator
{

ClassMethod run() [ Language = python ]
{
    import iris
    import math
    from collections import defaultdict
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

    GROUP BY
        l.addressCity,
        l.addressState,
        GetProp(GetJSON(ro.ResourceString,'code'),'text'),
        o.code,
        c.code,
        GetProp(GetJSON(rd.ResourceString,'code'),'text'),
        dr.code

    ORDER BY
        Occurrences DESC
    """

    rs = iris.sql.exec(sql)

    region_features = defaultdict(lambda: defaultdict(float))
    vocabulary = {}

    for row in rs:
        city = row[0] or "UNKNOWN_CITY"
        state = row[1] or "UNKNOWN_STATE"
        observation_name = row[2] or ""
        observation_code = row[3] or ""
        condition_code = row[4] or ""
        diagnostic_name = row[5] or ""
        occurrences = float(row[6] or 0)

        region_key = city + "|" + state

        features = [
            "OBS_NAME:" + observation_name,
            "OBS_CODE:" + observation_code,
            "COND_CODE:" + condition_code,
            "REPORT_NAME:" + diagnostic_name
        ]

        for feature in features:
            if feature.strip() == "":
                continue

            if feature not in vocabulary:
                vocabulary[feature] = len(vocabulary)

            region_features[region_key][feature] += occurrences

    insert_stmt = iris.sql.prepare("""
        INSERT INTO dc.RegionClinicalVector
        (
            AddressCity,
            AddressState,
            VectorVersion,
            ClinicalVector,
            FeatureCount,
            TotalOccurrences
        )
        VALUES (?, ?, ?, TO_VECTOR(?, DOUBLE), ?, ?)
    """)
    vector_version = "clinical-region-v1"

    for region_key, features_map in region_features.items():
        city, state = region_key.split("|", 1)

        vector = [0.0] * len(vocabulary)

        total_occurrences = 0.0

        for feature, value in features_map.items():
            index = vocabulary[feature]
            vector[index] = value
            total_occurrences += value

        norm = math.sqrt(sum(v * v for v in vector))
        if norm > 0:
            vector = [v / norm for v in vector]

        vector_str = "[" + ",".join(str(v) for v in vector) + "]"

        insert_stmt.execute(
            city,
            state,
            vector_version,
            vector_str,
            len(vocabulary),
            total_occurrences
        )
}

}