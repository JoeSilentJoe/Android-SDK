package filedownloadandroiddemo.filedownloadandroid.example.com.filedownloadandroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.ThreadPoolService;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.files.BackendlessFileAndroid;

import java.io.*;

public class MainActivity extends AppCompatActivity {

  TextView out;
  Button Download1;
  Button Download2;
  Button Download3;
  Button Download4;
  Button Download5;
  Button Download6;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Backendless.setUrl( Defaults.SERVER_URL );
    Backendless.initApp( getApplicationContext(), Defaults.APPLICATION_ID, Defaults.API_KEY );

    out = findViewById(R.id.out);
    Download1 = findViewById( R.id.Download1 );
    Download2 = findViewById( R.id.Download2 );
    Download3 = findViewById( R.id.Download3 );
    Download4 = findViewById( R.id.Download4 );
    Download5 = findViewById( R.id.Download5 );
    Download6 = findViewById( R.id.Download6 );

    View.OnClickListener ButtonDownload1 = new View.OnClickListener()
    {
      @Override
      public void onClick(View v) {
        ThreadPoolService.getPoolExecutor().execute(new Runnable()
        {
          @Override
          public void run()
          {
            downloadWithLocalPath();
          }
        });
        out.setText( "File downloading with local path" );
      }
    };
    Download1.setOnClickListener(ButtonDownload1);

    View.OnClickListener ButtonDownload2 = new View.OnClickListener()
    {
      @Override
      public void onClick(View v)
      {
        ThreadPoolService.getPoolExecutor().execute(new Runnable()
        {
          @Override
          public void run() {
            downloadWithOutputStream();
          }
        });
        out.setText( "File downloading with OutputStream" );
      }
    };
    Download2.setOnClickListener(ButtonDownload2);

    View.OnClickListener ButtonDownload3 = new View.OnClickListener()
    {
      @Override
      public void onClick(View v) {
        ThreadPoolService.getPoolExecutor().execute(new Runnable()
        {
          @Override
          public void run()
          {
            downloadFileInArray();
          }
        });
        out.setText( "File downloading file in ByteArray" );
      }
    };
    Download3.setOnClickListener(ButtonDownload3);

    View.OnClickListener ButtonDownload4 = new View.OnClickListener()
    {
      @Override
      public void onClick(View v) {
        ThreadPoolService.getPoolExecutor().execute(new Runnable()
        {
          @Override
          public void run()
          {
            downloadWithLocalPathAndProgressBar();
          }
        });
        out.setText( "File downloading file downloading with local path and ProgressBar" );
      }
    };
    Download4.setOnClickListener(ButtonDownload4);

    View.OnClickListener ButtonDownload5 = new View.OnClickListener()
    {
      @Override
      public void onClick(View v) {
        ThreadPoolService.getPoolExecutor().execute(new Runnable()
        {
          @Override
          public void run()
          {
            downloadWithOutputStreamAndProgressBar();
          }
        });
        out.setText( "File downloading with OutputStream and ProgressBar" );
      }
    };
    Download5.setOnClickListener(ButtonDownload5);

    View.OnClickListener ButtonDownload6 = new View.OnClickListener()
    {
      @Override
      public void onClick(View v) {
        ThreadPoolService.getPoolExecutor().execute(new Runnable()
        {
          @Override
          public void run()
          {
            downloadFileInByteArrayWithProgressBar();
          }
        });
        out.setText( "File downloading file in ByteArray with ProgressBar" );
      }
    };
    Download6.setOnClickListener(ButtonDownload6);
  }

  void downloadWithLocalPath()
  {
    Person person = Backendless.Data.of( Person.class ).findById( Defaults.ID_PERSON );

    File file = new File( getFilesDir(), Defaults.FILE_NAME );
    String localFilePathName = file.toString();
    person.image.download( localFilePathName, new AsyncCallback<File>()
    {
      @Override
      public void handleResponse(File response)
      {
        System.out.println( response.getName() );
      }

      @Override
      public void handleFault(BackendlessFault fault)
      {
        new BackendlessFault( fault );
      }
    });
  }

  void downloadWithOutputStream()
  {
    Person person = Backendless.Data.of( Person.class).findById( Defaults.ID_PERSON );

    File file = new File( getFilesDir(), Defaults.FILE_NAME );
    final OutputStream[] out = {null};
    try
    {
      out[0] = new FileOutputStream( file );
    }
    catch (FileNotFoundException e)
    {
      e.printStackTrace();
    }

    person.image.download(out[0], new AsyncCallback<Void>() {
      @Override
      public void handleResponse(Void response)
      {
        System.out.println( "Downloading complete" );
      }

      @Override
      public void handleFault( BackendlessFault fault )
      {
        new BackendlessFault( fault );
      }
    });
  }

  void downloadFileInArray() {
    Person person = Backendless.Data.of( Person.class).findById( Defaults.ID_PERSON );

    person.image.download( new AsyncCallback<byte[]>()
    {
      @Override
      public void handleResponse( byte[] response )
      {
        writeByteArray( response );
      }

      @Override
      public void handleFault( BackendlessFault fault )
      {
        new BackendlessFault( fault );
      }
    });
  }

  void downloadWithLocalPathAndProgressBar()
  {
    Person person = Backendless.Data.of( Person.class ).findById( Defaults.ID_PERSON );
    ProgressBar bar = findViewById( R.id.determinateBar );

    final File file = new File( getFilesDir(), Defaults.FILE_NAME );
    String localFilePathName = file.toString();

    BackendlessFileAndroid android = (BackendlessFileAndroid) person.image;
    android.download( localFilePathName, bar, new AsyncCallback<File>()
    {
      @Override
      public void handleResponse( File response )
      {
        System.out.println( response.toString() );
      }

      @Override
      public void handleFault( BackendlessFault fault )
      {
        new BackendlessFault( fault );
      }
    });

  }

  void downloadWithOutputStreamAndProgressBar()
  {
    Person person = Backendless.Data.of( Person.class).findById( Defaults.ID_PERSON );
    ProgressBar bar = findViewById(R.id.determinateBar);

    File file = new File( getFilesDir(), Defaults.FILE_NAME );
    final OutputStream[] out = {null};
    try
    {
      out[0] = new FileOutputStream( file );
    } catch (FileNotFoundException e)
    {
      e.printStackTrace();
    }

    BackendlessFileAndroid android = (BackendlessFileAndroid) person.image;
    android.download( out[0], bar, new AsyncCallback<Void>()
    {
      @Override
      public void handleResponse( Void response )
      {
        System.out.println( "Downloading complete" );
      }

      @Override
      public void handleFault( BackendlessFault fault )
      {
        new BackendlessFault( fault );
      }
    });
  }

  void downloadFileInByteArrayWithProgressBar()
  {
    Person person = Backendless.Data.of( Person.class).findById( Defaults.ID_PERSON );
    ProgressBar bar = findViewById( R.id.determinateBar );

    BackendlessFileAndroid android = (BackendlessFileAndroid) person.image;
    android.download( bar, new AsyncCallback<byte[]>()
    {
      @Override
      public void handleResponse( byte[] response )
      {
        writeByteArray( response );
      }

      @Override
      public void handleFault( BackendlessFault fault )
      {
        new BackendlessFault( fault );
      }
    });
  }

  void writeByteArray( byte[] bytes )
  {
    File file = new File( getFilesDir(), Defaults.FILE_NAME );
    FileOutputStream stream = null;
    try
    {
      stream = new FileOutputStream(file);
      stream.write(bytes);
    }
    catch ( IOException e )
    {
      e.printStackTrace();
    }
    finally
    {
      try
      {
        if (stream != null)
        {
          stream.close();
        }
      }
      catch ( IOException e )
      {
        e.printStackTrace();
      }
    }
  }

}

