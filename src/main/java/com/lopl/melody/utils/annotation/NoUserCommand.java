package com.lopl.melody.utils.annotation;

import com.lopl.melody.slash.SlashCommandClient;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * You can mark a SlashCommand class with this annotation and it will no longer be able to be executed.
 * Ait will be updated when applying the changes with an upsert at {@link SlashCommandClient}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {CONSTRUCTOR, MODULE, TYPE})
public @interface NoUserCommand {
}
