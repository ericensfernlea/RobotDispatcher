package com.fernleaflowers.n3DispatcherTestV2Final;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public Retrofit retrofit;

    /*public static RobotAPI getNames() {

        retrofit = new Retrofit.Builder()
                .baseUrl("http://127.0.0.1:5000/fleet-api/v1.0/robots/")
                .build();

        //Creating object for our interface
        ApiInterface api = adapter.create(ApiInterface.class);
        return api;
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
    }


    Button btn;
    AsyncTask<Void, Void, String> runningTask;


    @Override
    public void onClick(View view) {
        // Detect the view that was "clicked"
        switch (view.getId()) {
            case R.id.btnSubmitRobot:
               Dispatch();
                break;
            case R.id.btnRobotTest:
                RunTests();
                break;
        }
    }

    public void Dispatch()
    {
        String robotName = ConvertName();
        if(robotName == null)
            return;

        String robotPath = ((EditText)findViewById(R.id.editRobotPath)).getText().toString();
        if(robotPath.length() == 0 || robotPath == null)
            robotPath = "n3-11";
        boolean result = SendPathRequest(robotName,robotPath);

        EditText nEdit   = (EditText)findViewById(R.id.editRobotNumber);
        nEdit.setText("");
        EditText pEdit   = (EditText)findViewById(R.id.editRobotPath);
        pEdit.setText("");

        if(!result)
        {
            AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
            dlgAlert.setMessage("Error: Path not sent");
            dlgAlert.setTitle("Dispatch Error");
            dlgAlert.setPositiveButton("OK", null);
            dlgAlert.setCancelable(true);
            dlgAlert.create().show();
        }
    }


    public String ConvertName()
    {
        String name = "";
        int robotNumber;
        EditText mEdit   = (EditText)findViewById(R.id.editRobotNumber);
        String rawName = mEdit.getText().toString();

        if(rawName.length() == 0)
            return null;

        try {
           robotNumber  = Integer.parseInt(rawName);
        } catch(NumberFormatException nfe) {
            Toast.makeText(MainActivity.this, "Please enter the exact number of the robot, no text", Toast.LENGTH_SHORT).show();
            return null;
        }

        if(robotNumber > 0)
        {
            if(robotNumber < 10)
            {
                name = "tug_0" + robotNumber;
                return name;
            }
            else
            {
                name = "tug_" + robotNumber;
                return name;
            }

        }
        else
        {
            Toast.makeText(MainActivity.this, "Please enter a numerical robot number, greater than Zero", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    public void RunTests()
    {
        String robotName = ConvertName();


        String testOutcome = "1. GetRobotNames: ";
        //begin tests
        if(robotName != null)
        {
            //getnames
            List<String> names = GetRobotNames();

            if(names.isEmpty())
                testOutcome += "Names Empty - Failed\n";
            else if(names.get(0).equals("No Robots Found"))
                testOutcome += "Robots Not Found - Success\n";
            else
            {
                if(names.contains(robotName))
                    testOutcome += "Robot Found - Success\n";
            }
        }
        else
        {
            testOutcome += "Robot number not provided - Failed\n";
        }



        //sendpathrequest
        //boolean pathTest = SendPathRequest("tug_11", "test_path");
        //if(pathTest)
        //    testOutcome += "SendPathRequest Success";
        //else
        //   testOutcome += "SendPathRequest Failure";

        //getattributes

        EditText nEdit   = (EditText)findViewById(R.id.editRobotNumber);
        nEdit.setText("");
        EditText pEdit   = (EditText)findViewById(R.id.editRobotPath);
        pEdit.setText("");


        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
        dlgAlert.setMessage(testOutcome);
        dlgAlert.setTitle("Dispatch Test");
        dlgAlert.setPositiveButton("OK", null);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();

    }



    public List<String> GetRobotNames()
    {
         List<String> names = new ArrayList<String>();
        Names namesRaw;

        //get Retrofit API Instance
        RobotAPI service = RetrofitInstance.getRetrofitInstance().create(RobotAPI.class);

        /** Call the method with parameter in the interface to get the notice data*/
        Call<Names> call = service.getNames();

        /**Log the URL called*/
        Log.e("URL Called", call.request().url() + "");

        try
        {
            Response<Names> response = call.execute();
            Log.e("URL Response", response.toString());
            //Toast.makeText(MainActivity.this, response.body().toString(), Toast.LENGTH_LONG).show();

            if(response.code() == 401)
            {
                return names;
            }

            namesRaw = response.body();
            names.addAll(namesRaw.getNames());


            if(names.size() == 0)
            {
                names.add("No Robots Found");
                return names;
            }

        }
        catch (SocketTimeoutException ex)
        {
            ex.printStackTrace();
            Toast.makeText(MainActivity.this, "Error Reaching API", Toast.LENGTH_SHORT).show();
            return names;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Toast.makeText(MainActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();
            return names;
        }




        return names;
        /*call.enqueue(new Callback<Robots>() {
            @Override
            public void onResponse(Call<Robots> call, Response<Robots> response) {
                //log call response
                Log.e("URL Response", response.toString());
                names.addAll(response.body().getNames());
                Toast.makeText(MainActivity.this, "Success! Names Retrieved", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Robots> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Something went wrong...Error message: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                names.add("No Robots Found");

            }
        });*/


    }

    public boolean SendPathRequest(String robotName, String path)
    {
        List<String> names = null;
        PathJSON pathJSON = new PathJSON(path, "1");
        //String fullPath = "{\"path\":{\"path-name\":\"" + path + "\",\"task-id\":\"1\"}}";
        //TypedInput in = new TypedByteArray("application/json", json.getBytes("UTF-8"));


        //get Retrofit API Instance
        RobotAPI service = RetrofitInstance.getRetrofitInstance().create(RobotAPI.class);

        /** Call the method with parameter in the interface to get the notice data*/
        Call<String> call = service.sendPathRequest(robotName,pathJSON);

        /**Log the URL called*/
        Log.e("URL Called", call.request().url() + "");

        try
        {
            Response<String> response = call.execute();
            Log.e("URL Response", response.toString());

            if(response.code() == 401)
            {
                Toast.makeText(MainActivity.this, "Error Reaching API", Toast.LENGTH_SHORT).show();
                return false;
            }
            else
            {
                if(response.body().equals("Success"))
                {
                    Toast.makeText(MainActivity.this, "Path: " + path + " sent successfully to Robot: " + robotName, Toast.LENGTH_LONG).show();
                    return true;
                }
                else
                    return false;
            }
        }
        catch (SocketTimeoutException ex)
        {
            ex.printStackTrace();
            Toast.makeText(MainActivity.this, "Error Reaching API", Toast.LENGTH_SHORT).show();
            return false;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }

        /*call.enqueue(new Callback<Robots>() {
            @Override
            public void onResponse(Call<Robots> call, Response<Robots> response) {
                //log call response
                Log.e("URL Response", response.toString());
                Toast.makeText(MainActivity.this, "Success! Path Sent", Toast.LENGTH_SHORT).show();
                callStatus[0] = true;
            }

            @Override
            public void onFailure(Call<Robots> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Something went wrong...Error message: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });*/
    }

    public String GetLastCommand()
    {

        return "Last Command: Robot 5 Dispatched to N3 Loop";
    }

    public String GetRobotStatus(String robotName)
    {
        List<String> names = null;

        //get Retrofit API Instance
        RobotAPI service = RetrofitInstance.getRetrofitInstance().create(RobotAPI.class);

        /** Call the method with parameter in the interface to get the notice data*/
        Call<Names> call = service.getNames();

        /**Log the URL called*/
        Log.e("URL Called", call.request().url() + "");


        /*call.enqueue(new Callback<Robots>() {
            @Override
            public void onResponse(Call<Robots> call, Response<Robots> response) {
                //log call response
                Log.e("URL Response", response.toString());
                names.addAll(response.body().getNames());
                Toast.makeText(MainActivity.this, "Success! Names Retrieved", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Robots> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Something went wrong...Error message: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                names.add("No Robots Found");
            }
        });
*/




        return names.get(0);
    }

    public void GetAllStatus()
    {

    }


}