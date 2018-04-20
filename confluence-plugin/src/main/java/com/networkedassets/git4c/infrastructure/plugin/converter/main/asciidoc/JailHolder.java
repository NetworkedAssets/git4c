package com.networkedassets.git4c.infrastructure.plugin.converter.main.asciidoc;

import org.jetbrains.annotations.Nullable;

public interface JailHolder {

    void set_jail_location(@Nullable String jailLocation);

    void clear_jail_location();

}
