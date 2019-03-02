package com.stefankopieczek.audinance.renderer;

public class RenderingFailure extends RuntimeException
{
    public RenderingFailure()
    {
        super();
    }

    public RenderingFailure(String message)
    {
        super(message);
    }

    public RenderingFailure(Throwable t)
    {
        super(t);
    }

    public RenderingFailure(String message, Throwable t)
    {
        super(message, t);
    }
}
