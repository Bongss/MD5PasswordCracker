package PasswordCracker;

import com.facebook.presto.spi.function.Description;
import com.facebook.presto.spi.function.ScalarFunction;
import com.facebook.presto.spi.function.SqlType;
import com.facebook.presto.spi.type.StandardTypes;
import io.airlift.slice.Slice;
import io.airlift.slice.Slices;

import java.security.MessageDigest;

import static PasswordCracker.CrackerUtil.encryptPassword;
import static PasswordCracker.CrackerUtil.getMessageDigest;
import static java.nio.charset.StandardCharsets.UTF_8;

public class EncryptFunction {
    public EncryptFunction() {}

    @Description("Returns the String representation of the MD5 encrypt of the password")
    @ScalarFunction("encrypt")
    @SqlType(StandardTypes.VARCHAR)
    public static Slice encrypt(@SqlType(StandardTypes.VARCHAR) Slice password)
    {
        MessageDigest messageDigest = getMessageDigest();
        String encryptedPassword = encryptPassword(password.toStringUtf8(), messageDigest);
        return Slices.utf8Slice(encryptedPassword);
    }
}
