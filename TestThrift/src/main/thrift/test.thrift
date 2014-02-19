namespace java thrift.test

struct TestMessage 
{
	1: required string topic
	2: required binary content
	3: required i64    createdTime
	4: required string id
	5: optional string ipAddress
	6: optional map<string,string> props
}

service TestService 
{
	i64 ping(1: i32 length, 2: TestMessage msg)
}