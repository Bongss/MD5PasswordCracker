namespace java thrift.gen.PasswordCrackerWorkerService

typedef i32 int
typedef i64 long

service PasswordCrackerWorkerService
{
	string startFindPasswordInRange(1:long rangeBegin, 2:long rangeEnd, 3:string encryptedPassword),
	void reportTermination(1:string jobId)
}