package com.backendless.files;

import com.backendless.ThreadPoolService;
import com.backendless.exceptions.BackendlessException;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

class FileDownload
{

  private final static String FILE_DOWNLOAD_ERROR = "Could not downloading a file";

  Future<File> download( final String fileURL, final String destinationPath, final ProgressCallback progressCallback )
  {
    Callable<File> downloadTask = new Callable<File>()
    {
      @Override
      public File call()
      {
        return downloading( fileURL, destinationPath, progressCallback );
      }
    };
    return ThreadPoolService.getPoolExecutor().submit( downloadTask );
  }

  Future<File> download( final String fileURL, final String destinationPath, final String fileName,
                         final ProgressCallback progressCallback )
  {
    Callable<File> downloadTask = new Callable<File>()
    {
      @Override
      public File call()
      {
        return downloading( fileURL, destinationPath, fileName, progressCallback );
      }
    };
    return ThreadPoolService.getPoolExecutor().submit( downloadTask );
  }

  Future<Void> download( final String fileURL, final OutputStream stream, final ProgressCallback progressCallback )
  {
    Callable<Void> downloadTask = new Callable<Void>()
    {
      @Override
      public Void call()
      {
        downloading( fileURL, stream, progressCallback );
        return null;
      }
    };
    return ThreadPoolService.getPoolExecutor().submit( downloadTask );
  }

  Future<byte[]> download( final String fileURL, final ProgressCallback progressCallback )
  {
    Callable<byte[]> downloadTask = new Callable<byte[]>()
    {
      @Override
      public byte[] call()
      {
        return downloading( fileURL, progressCallback );
      }
    };
    return ThreadPoolService.getPoolExecutor().submit( downloadTask );
  }

  private byte[] downloading( String fileURL, ProgressCallback progressCallback )
  {
    byte[] byteArray;
    try( ByteArrayOutputStream out = new ByteArrayOutputStream() )
    {
      downloading( fileURL, out, progressCallback );
      byteArray = out.toByteArray();
    }
    catch( IOException e )
    {
      throw new BackendlessException( FILE_DOWNLOAD_ERROR, e.getMessage() );
    }

    return byteArray;
  }

  private File downloading( String fileURL, String destinationPath, ProgressCallback progressCallback )
  {
    String fileName = fileURL.substring( fileURL.lastIndexOf( '/' ) + 1 );
    return downloading( fileURL, destinationPath, fileName, progressCallback );
  }

  private File downloading( String fileURL, String destinationPath, String fileName, ProgressCallback progressCallback )
  {
    String localFilePathName = destinationPath + fileName;
    final File file = new File( localFilePathName );

    try( BufferedOutputStream out = new BufferedOutputStream( new FileOutputStream( file ) ) )
    {
      downloading( fileURL, out, progressCallback );
    }
    catch( IOException e )
    {
      throw new BackendlessException( FILE_DOWNLOAD_ERROR, e.getMessage() );
    }

    return file;
  }

  private void downloading( String fileURL, OutputStream out, ProgressCallback progressCallback )
  {
    URL url;
    try
    {
      url = new URL( fileURL );
    }
    catch( MalformedURLException e )
    {
      throw new BackendlessException( FILE_DOWNLOAD_ERROR, e.getMessage() );
    }
    try( InputStream in = url.openStream() )
    {
      int fileSize = url.openConnection().getContentLength();
      int countReadSize = 0;
      progressCallback.setOnProgress( 0 );
      int count;
      byte[] buffer = new byte[ 4096 ];
      while( ( count = in.read( buffer ) ) > 0 )
      {
        if( Thread.currentThread().isInterrupted() )
        {
          break;
        }
        out.write( buffer, 0, count );
        countReadSize += count;
        progressCallback.setOnProgress( countReadSize * 100 / fileSize );
      }
    }
    catch( IOException e )
    {
      throw new BackendlessException( FILE_DOWNLOAD_ERROR, e.getMessage() );
    }
  }

}