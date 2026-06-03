Class dc.RegionClinicalVector Extends %Persistent
{

Property AddressCity As %String(MAXLEN = 120);

Property AddressState As %String(MAXLEN = 10);

Property VectorVersion As %String(MAXLEN = 80);

Property ClinicalVector As %Library.Vector(LEN = 384);

Property FeatureCount As %Integer;

Property TotalOccurrences As %Double;

Property IdentifiedDiseases As %String(MAXLEN = "");

Property IdentifiedDiseaseCodes As %String(MAXLEN = "");

Property MainSymptoms As %String(MAXLEN = "");

Property TopFeatures As %String(MAXLEN = "");

Property ClinicalSummary As %String(MAXLEN = "");

Property LastCalculatedAt As %TimeStamp [ InitialExpression = {$ZDATETIME($HOROLOG,3)} ];


Storage Default
{
<Data name="RegionClinicalVectorDefaultData">
<Value name="1">
<Value>%%CLASSNAME</Value>
</Value>
<Value name="2">
<Value>AddressCity</Value>
</Value>
<Value name="3">
<Value>AddressState</Value>
</Value>
<Value name="4">
<Value>VectorVersion</Value>
</Value>
<Value name="5">
<Value>ClinicalVector</Value>
</Value>
<Value name="6">
<Value>FeatureCount</Value>
</Value>
<Value name="7">
<Value>TotalOccurrences</Value>
</Value>
<Value name="8">
<Value>IdentifiedDiseases</Value>
</Value>
<Value name="9">
<Value>IdentifiedDiseaseCodes</Value>
</Value>
<Value name="10">
<Value>MainSymptoms</Value>
</Value>
<Value name="11">
<Value>TopFeatures</Value>
</Value>
<Value name="12">
<Value>ClinicalSummary</Value>
</Value>
<Value name="13">
<Value>LastCalculatedAt</Value>
</Value>
</Data>
<DataLocation>^dc.RegionClinicalVectorD</DataLocation>
<DefaultData>RegionClinicalVectorDefaultData</DefaultData>
<IdLocation>^dc.RegionClinicalVectorD</IdLocation>
<IndexLocation>^dc.RegionClinicalVectorI</IndexLocation>
<StreamLocation>^dc.RegionClinicalVectorS</StreamLocation>
<Type>%Storage.Persistent</Type>
}

}