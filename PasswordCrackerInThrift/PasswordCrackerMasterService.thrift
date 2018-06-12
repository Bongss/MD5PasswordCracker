namespace java thrift.gen.PasswordCrackerMasterService

typedef i32 int
typedef i64 long

service PasswordCrackerMasterService
{
	string decrypt(1:string encryptedPassword),
	void reportHeartBeat(1:string ipAddress),
}




