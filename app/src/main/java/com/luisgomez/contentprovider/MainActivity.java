package com.luisgomez.contentprovider;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView item;
    private Button btnConsultarLLamadas;


    //Importante: para ver que funciona, hay que agregar llamadas o contactos en el emulador

    private ListView listaLlamadas;

    // Request code for READ_CONTACTS. It can be any number > 0.

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find the list view
        this.listaLlamadas = findViewById(R.id.listaLlamadas);

        //Referencias a los controles

        btnConsultarLLamadas = findViewById(R.id.consultar);
        btnConsultarLLamadas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //y nos vamos al metodo
                showCalls();
            }
        });

    }

    private void showCalls() {
        // se comprueba la version del sdk, y en funcion de ellos nos va a pedir permisos o no.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CALL_LOG}, PERMISSIONS_REQUEST_READ_CONTACTS);

        } else {
            //En caso de que  la version sea menor de 6.0, los permisos ya estan agregados
            List<String> calls = getCallName();
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, calls);
            listaLlamadas.setAdapter(adapter);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS){
            if (grantResults[0] == getPackageManager().PERMISSION_GRANTED) {
                showCalls();
            } else {
                Toast.makeText(this, "Debes aceptar los permisos para poder ver las llamadas", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private List<String> getCallName(){
        List<String> calls = new ArrayList<>();
        // Get the ContentResolver
        ContentResolver cr = getContentResolver();
        // Get the Cursor of all the calls
        Cursor cursor = cr.query(CallLog.Calls.CONTENT_URI,null,null,null, null);

        // Move the cursor to first. Also check whether the cursor is empty or not.
        if (cursor.moveToFirst()){
            // Iterate through the cursor
            do {
                //Get the call name
                String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
                calls.add(number);
            } while (cursor.moveToNext());

        }

        //Cerramos el cursor
        cursor.close();

        return calls;
    }

}
