package com.redshape.generators.jpa;

public class ClasspathBuilderException
    extends Exception
{
    public ClasspathBuilderException( String message, Throwable t )
    {
        super( message, t );
    }

    public ClasspathBuilderException( String message )
    {
        super( message );
    }
}