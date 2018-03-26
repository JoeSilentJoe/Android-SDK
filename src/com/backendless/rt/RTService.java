package com.backendless.rt;

import com.backendless.async.callback.Fault;
import com.backendless.async.callback.Result;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.rt.data.EventHandlerFactory;

import java.util.concurrent.CopyOnWriteArrayList;

public class RTService
{
  private final RTClient rtClient = RTClientFactory.get();

  private final CopyOnWriteArrayList<Result<Void>> connectListeners = new CopyOnWriteArrayList<>();
  private final CopyOnWriteArrayList<Result<String>> disconnectListeners = new CopyOnWriteArrayList<>();
  private final CopyOnWriteArrayList<Result<ReconnectAttempt>> reconnectListeners = new CopyOnWriteArrayList<>();
  private final CopyOnWriteArrayList<Fault> connectErrorListeners = new CopyOnWriteArrayList<>();

  public RTService()
  {
    if(rtClient.isAvailable())
    {
      rtClient.setConnectEventListener( new Result<Void>()
      {
        @Override
        public void handle( Void result )
        {
          RTService.this.handle( result, connectListeners );
        }
      } );

      rtClient.setDisconnectEventListener( new Result<String>()
      {
        @Override
        public void handle( String result )
        {
          RTService.this.handle( result, disconnectListeners );
        }
      } );

      rtClient.setConnectErrorEventListener( new Fault()
      {
        @Override
        public void handle( BackendlessFault fault )
        {
          RTService.this.handle( fault, connectErrorListeners );
        }
      } );

      rtClient.setReconnectAttemptEventListener( new Result<ReconnectAttempt>()
      {
        @Override
        public void handle( ReconnectAttempt result )
        {
          RTService.this.handle( result, reconnectListeners );
        }
      } );
    }
  }

  public final EventHandlerFactory Data = new EventHandlerFactory();

  public void addConnectEventListener( Result<Void> callback )
  {
    if( rtClient.isConnected() )
      callback.handle( null );

    connectListeners.add( callback );
  }

  public void addReconnectAttemptEventListener( Result<ReconnectAttempt> callback )
  {
    reconnectListeners.add( callback );
  }

  public void addConnectErrorEventListener( Fault fault )
  {
    connectErrorListeners.add( fault );
  }

  public void addDisconnectEventListener( Result<String> callback )
  {
    disconnectListeners.add( callback );
  }

  @SuppressWarnings( "unchecked" )
  public <T extends Result> void removeEventListeners( T callback )
  {
    if( connectErrorListeners.contains( callback ) )
    {
      removeEventListeners( (Fault) callback, connectErrorListeners );
    }
    else if( disconnectListeners.contains( callback ) )
    {
      removeEventListeners( callback, disconnectListeners );
    }
    else if( connectListeners.contains( callback ) )
    {
      removeEventListeners( callback, connectListeners );
    }
    else if( reconnectListeners.contains( callback ) )
    {
      removeEventListeners( callback, reconnectListeners );
    }
  }

  public void connect()
  {
    rtClient.connect();
  }

  public void disconnect()
  {
    rtClient.disconnect();
  }

  private <T extends Result> void removeEventListeners( T callback, CopyOnWriteArrayList<T> listeners )
  {
    for( T listener : listeners )
    {
      if( listener == callback )
      {
        //we can do it because it is CopyOnWriteArrayList so we iterate through the copy
        listeners.remove( callback );
      }
    }
  }

  @SuppressWarnings( "unchecked" )
  private <T, E extends Result> void handle( T result, CopyOnWriteArrayList<E> listeners )
  {
    for( E listener : listeners )
    {
      listener.handle( result );
    }
  }
}