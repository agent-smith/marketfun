package com.agentsmith.marketfun;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

import org.apache.commons.validator.routines.EmailValidator;

/**
 * Validator for email addresses.
 * <p/>
 * User: rmarquez
 * Date: 1/12/14
 * Time: 3:45 PM
 */
public class MyEmailValidator implements IParameterValidator
{
    public void validate(String name, String value) throws ParameterException
    {
        if (!EmailValidator.getInstance().isValid(value)) {
            throw new ParameterException("\"" + value + "\" is not a valid email address");
        }
    }
}
