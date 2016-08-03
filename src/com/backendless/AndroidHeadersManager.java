package com.backendless;

import com.backendless.commons.DeviceType;
import com.backendless.exceptions.BackendlessException;
import com.backendless.exceptions.ExceptionMessage;

import java.util.Hashtable;
import java.util.Map;


public class AndroidHeadersManager implements IHeadersManager
{
  private static volatile AndroidHeadersManager instance;
  private final Object headersLock = new Object();
  private Hashtable<String, String> headers = new Hashtable<String, String>();

  private AndroidHeadersManager()
  {
    if( Backendless.getApplicationId() == null || Backendless.getSecretKey() == null )
    {
      throw new IllegalStateException( ExceptionMessage.NOT_INITIALIZED );
    }
    addHeader( HeadersManager.HeadersEnum.APP_ID_NAME, Backendless.getApplicationId() );
    addHeader( HeadersManager.HeadersEnum.SECRET_KEY_NAME, Backendless.getSecretKey() );
    addHeader( HeadersManager.HeadersEnum.APP_TYPE_NAME, DeviceType.ANDROID.name() );
    addHeader( HeadersManager.HeadersEnum.API_VERSION, "1.0" );
    addHeaders( Backendless.getHeaders() );
  }

  static AndroidHeadersManager getInstance() throws BackendlessException
  {
    if( instance == null )
      synchronized( HeadersManager.class )
      {
        if( instance == null )
        {
          instance = new AndroidHeadersManager();
        }
      }
    return instance;
  }

  public void cleanHeaders()
  {
    synchronized( headersLock )
    {
      instance = null;
    }
  }

  public void addHeader( HeadersManager.HeadersEnum headersEnum, String value )
  {
    synchronized( headersLock )
    {
      headers.put( headersEnum.getHeader(), value );
    }
  }

  public void addHeaders( Map<String, String> headers )
  {
    if( headers == null || headers.isEmpty() )
      return;

    synchronized( headersLock )
    {
      this.headers.putAll( headers );
    }
  }

  public void removeHeader( HeadersManager.HeadersEnum headersEnum )
  {
    synchronized( headersLock )
    {
      headers.remove( headersEnum.getHeader() );
    }
  }

  public Hashtable<String, String> getHeaders() throws BackendlessException
  {
    synchronized( headersLock )
    {
      if( headers == null || headers.isEmpty() )
        throw new IllegalStateException( ExceptionMessage.NOT_INITIALIZED );

      return headers;
    }
  }

  public void setHeaders( Map<String, String> headers )
  {
    synchronized( headersLock )
    {
      this.headers.putAll( headers );
    }
  }

  public String getHeader( HeadersManager.HeadersEnum headersEnum ) throws BackendlessException
  {
    synchronized( headersLock )
    {
      if( headers == null || headers.isEmpty() )
        throw new IllegalStateException( ExceptionMessage.NOT_INITIALIZED );

      String header = headers.get( headersEnum );

      if( header == null )
        header = headers.get( headersEnum.getHeader() );

      return header;
    }
  }
}
