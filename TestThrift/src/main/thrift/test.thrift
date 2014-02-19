namespace java thrift.test

struct TestMessage 
{
	1: string topic
	2: binary content
	3: i64    createdTime
	4: string id
	5: string ipAddress
	6: map<string,string> props
}

service TestService 
{
	i64 ping(1: i32 length, 2: TestMessage msg)
}