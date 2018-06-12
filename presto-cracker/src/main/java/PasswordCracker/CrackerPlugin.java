package PasswordCracker;

import com.facebook.presto.spi.Plugin;
import com.facebook.presto.spi.connector.ConnectorFactory;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.Set;

public class CrackerPlugin implements Plugin {
    @Override
    public Iterable<ConnectorFactory> getConnectorFactories() {
        return ImmutableList.of(new CrackerConnectorFactory());
    }

    @Override
    public Set<Class<?>> getFunctions() {
        return ImmutableSet.of(EncryptFunction.class);
    }
}
