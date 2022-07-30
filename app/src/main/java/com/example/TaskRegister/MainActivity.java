package com.example.TaskRegister;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;

import com.example.TaskRegister.ui.filter.OrdenarPendientes;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Calendar;

// Esta es la clase principal, la primera en ser ejecutada.

// Las líneas que tienen *Android Def o <Android Def> </Android Def> son las que fueron generadas
// por Android Studio. Total: 22 líneas aproximadamente.

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration; // *Android Def
    private static final String FILE_NAME_ACTIVE_TASKS = "activeTasks.txt";
    private static final String FILE_NAME_COMPLETED_TASKS = "completedTasks.txt";
    private static String day;
    private static int currentNumericDate;
    private static boolean paintButtons, paintTasks, confirmCompleted, compactView;

    // Este método se llama cuando se crea la actividad. La actividad es la pantalla que se muestra
    // a un usuario de android.
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // <Android Def>
        // Siempre se tiene que llamar este método cuando se usa onCreate();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Permite que el contenido del XML se muestre.
        Toolbar toolbar = findViewById(R.id.toolbar); // Es la toolbar de arriba.
        setSupportActionBar(toolbar);
        // </Android Def>

        initializeInformation();
        initializeDay(); // Asigna el valor de day, que tendrá el día actual.

        // Estás líneas acceden a datos guardados por el usuario y se usan para acceder a la
        // información que se modifica desde la pestaña de ajustes (dentro de la aplicación).
        SharedPreferences preferences = getSharedPreferences("settings", Context.MODE_PRIVATE);
        // El segundo argumento de getBoolean() es el valor default en caso de que no haya nada
        // guardado. Esta línea asigna el valor de urgencySelected, que es utilizado para determinar
        // si primero se ordenan los pendientes por fecha o prioridad.
        OrdenarPendientes.setUrgencySelected(preferences.getBoolean("urgency", false));

        // Se consiguen los otros datos guardados por el usuario (guardados desde la actividad de
        // ajustes de esta aplicación). El valor default es true.
        // getBoolean: arg1: key, arg2: defValue
        paintButtons = preferences.getBoolean("btn_color", true);
        paintTasks = preferences.getBoolean("task_color", true);
        confirmCompleted = preferences.getBoolean("confirm_completed", true);
        compactView = preferences.getBoolean("compact_view", false);

        // El botón para ir a la actividad donde se pone la información.
        FloatingActionButton fab = findViewById(R.id.fab);

        // El método que se ejecuta al presionar el botón. Abre la actividad para poner la
        // información de los pendientes nuevos.
        fab.setOnClickListener(new View.OnClickListener() { // *Android Def
            @Override
            public void onClick(View view) {
                // Los objetos tipo Intent se utilizan para iniciar nuevas actividades.
                Intent registerActivity = new Intent(MainActivity.this,
                        RegisterData.class);
                // Le indica a la actividad que se usará para registrar pendientes.
                registerActivity.putExtra("action", "RegisterTask");
                startActivity(registerActivity);  // Inicia la actividad para registrar información.
            }
        });
        // <Android Def>
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Este método permite que cuando se navegue a uno de los fragmentos (cuyos ids están en él)
        // se pueda regresar haciendo uso del botón de 3 líneas. Este método indica que el fragmento
        // es de nivel superior. (Fragmento es el nombre que se le da a algo que es mostrado en un
        // contenedor).
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_finished_tasks)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        // </Android Def>
    }

    private void initializeDay() {
        Calendar calendar = Calendar.getInstance();

        // Se le agrega 1 al mes porque empieza en 0. Regresa la fecha en formato dd/mm/yy. La
        // última parte hace que solo se agreguen los 2 digitos finales del año en lugar de los 4.
        String day = calendar.get(Calendar.DAY_OF_MONTH) + "/" + (calendar.get(Calendar.MONTH)+1) +
                "/" + (calendar.get(Calendar.YEAR) + "").substring(2);

        // Le agrega los el 0 al día y mes.
        if (day.length() != 8){
            String[] formatDate = day.split("/");
            if (formatDate[0].length() == 1){
                day = "0" + day;
            }
            if (formatDate[1].length() == 1){
                day = day.substring(0,3) + "0" + day.substring(3);
            }
        }

        MainActivity.day = day;
        MainActivity.currentNumericDate = OrdenarPendientes.numericDate(day);
    }

    // id: 3
    public void onStop(){
        super.onStop(); // *Android Def
        saveInformation(FILE_NAME_ACTIVE_TASKS);
        saveInformation(FILE_NAME_COMPLETED_TASKS);
    }


    // Accede a los datos guardados de la aplicación y los envía al método que los carga a la
    // memoria ram, para que puedan ser utilizados en el programa.

    // Llamado cuando se abre la aplicación
    private void initializeInformation(){
        String[] files = fileList();// Regresa un arreglo con los nombres de los archivos existentes

        if (files.length == 0){
            // Si no hay archivos, entonces se tienen que crear por primera vez.
            createFile(FILE_NAME_ACTIVE_TASKS);
            createFile(FILE_NAME_COMPLETED_TASKS);
            files = fileList();
        } // Crea archivos si no existen

        BufferedReader activeT = null;
        BufferedReader completedT = null;

        try {
            // Se accede a la información guardada
            InputStreamReader file = new InputStreamReader(openFileInput(FILE_NAME_ACTIVE_TASKS));
            activeT = new BufferedReader(file);
            InputStreamReader file2 = new InputStreamReader(openFileInput(FILE_NAME_COMPLETED_TASKS));
            completedT = new BufferedReader(file2);

            // Envia los archivos al método de la clase Pendientes, para procesar la información.
            // Después de ejecutar este método ya se puede hacer uso de los HashMaps y acceder a la
            // información necesaria.

            Pendientes.initializeData(activeT, completedT); //

            // Se cierran los BufferedReaders.
            activeT.close();
            completedT.close();
        } catch(IOException ignored){

        }
    }

    // Crea los archivos para el celular que está usando la app, en caso de que no existan. Solo se
    // ejecutará una vez en cada instalación de la aplicación.

    private void createFile(String fileName) {
        try{
            // Crea un archivo y lo cierra.
            OutputStreamWriter fileCreate = new OutputStreamWriter(openFileOutput(fileName, Activity.MODE_PRIVATE));
            fileCreate.flush();
            fileCreate.close();
        } catch(IOException ignored){
            // Se le puso ese nombre a la excepción porque asi lo recomendó Android Studio.
        }
    }

    // Metodos para guardar la info. Pasan la información de la memoria ram a la persistente. Se
    // llaman en onStop() de la clase principal. Se utiliza el sistema de ficheros de andorid.
    private void saveInformation(String fileName){
        try{
            OutputStreamWriter saveFile = new OutputStreamWriter(openFileOutput(fileName,
                    Activity.MODE_PRIVATE)); // Se abre el archivo
            // Pendientes.retrieveDataText(fileName): regresa el texto completo de los pendientes
            // activos o terminados.
            saveFile.write(Pendientes.retrieveDataText(fileName)); // Se guarda toda la información.
            saveFile.flush();
            saveFile.close();    // Se libera memoria.
        } catch(IOException e) {
            System.out.println("Hubo un error, no se pudo guardar " + fileName);
        }
    }



    // <Android Def>
    // Este método crea el menu de la aplicación. Se le ponen las opciones de main.xml
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    // </Android Def>

    // Aquí se agregan las opciones del menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId())    {
            case R.id.action_settings:
                Intent settings = new Intent(this, SettingsActivity.class);
                startActivity(settings);
                return true;
            case R.id.activity_general_info:
                Intent generalInfo = new Intent(this, GeneralInformation.class);
                startActivity(generalInfo);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // <Android Def>
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
    // </Android Def>

    // Getters y setters

    public static String getDay(){
        return day;
    }

    public static int getCurrentNumericDate(){
        return currentNumericDate;
    }

    public static boolean isPaintButtons() {
        return paintButtons;
    }

    public static void setPaintButtons(boolean paintButtons) {
        MainActivity.paintButtons = paintButtons;
    }

    public static boolean isPaintTasks() {
        return paintTasks;
    }

    public static void setPaintTasks(boolean paintTasks) {
        MainActivity.paintTasks = paintTasks;
    }

    public static boolean isCompactView() {
        return compactView;
    }

    public static void setCompactView(boolean compactView) {
        MainActivity.compactView = compactView;
    }

    public static boolean isConfirmCompleted() {
        return confirmCompleted;
    }

    public static void setConfirmCompleted(boolean confirmCompleted) {
        MainActivity.confirmCompleted = confirmCompleted;
    }
}