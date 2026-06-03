Class dc.RegionClinicalVector Extends %Persistent
{
Property AddressCity As %String;
Property AddressState As %String;
Property VectorVersion As %String;
Property ClinicalVector As %Vector(DATATYPE = "DOUBLE");
Property FeatureCount As %Integer;
Property TotalOccurrences As %Numeric;
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
</Data>
<DataLocation>^dc.RegionClinicalVectorD</DataLocation>
<DefaultData>RegionClinicalVectorDefaultData</DefaultData>
<IdLocation>^dc.RegionClinicalVectorD</IdLocation>
<IndexLocation>^dc.RegionClinicalVectorI</IndexLocation>
<StreamLocation>^dc.RegionClinicalVectorS</StreamLocation>
<Type>%Storage.Persistent</Type>
}

}