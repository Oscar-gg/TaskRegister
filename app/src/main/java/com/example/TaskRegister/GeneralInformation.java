package com.example.TaskRegister;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

// Muestra la información general de la aplicación. Se accede a esta clase desde el menú.

public class GeneralInformation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_information);
        TextView textBody = findViewById(R.id.general_info_tv_body);

        // Le pone el título a la barra superior de la pantalla.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getResources().getString(R.string.action_settings2));
        }

        // Se pone el texto completo en un stringbuilder y luego se le manda al TextView.
        StringBuilder sb = new StringBuilder();

        sb.append("¡Bienvenido a la aplicación Registro de Tareas!").append("\n\n");

        sb.append("A continuación encontrarás información relevante acerca del funcionamiento de " +
                "esta aplicación:").append("\n\n");

        sb.append("Primero, los pendientes son desplegados en 2 secciones, en la de 'Pendientes' " +
                "y en la de 'Pendientes Terminados'.").append("\n\n");

        sb.append("Para mover un pendiente de una lista a otra, tienes que pulsar el cuadrado que se " +
                "encuentra a la izquierda del pendiente. Para ver la información de un pendiente " +
                "tienes que presionarlo en cualquiera de las listas y se abrirá en una nueva pantalla. ");
        sb.append("\n\n");

        sb.append("Para borrar un pendiente tienes que presionarlo y luego pulsar el botón 'borrar'." +
                " Algo importante a considerar es que los pendientes se borran para siempre.");

        sb.append("\n\n").append("Las listas de 'Categoría' y 'Responsables' varían conforme la " +
                "información que introduces. Estas listas muestran las opciones antes ingresadas " +
                "en estos campos. Entonces, es conveniente que no borres todos los pendientes terminados" +
                " ya que tendrías que volver a introducir las categorías manualmente.").append("\n\n");

        sb.append("Cuando registras una tarea, se pueden dejar todos los campos vacíos a excepción " +
                "del nombre. Cuando dejas los campos vacíos se guarda información por default. ");
        sb.append("Si se deja la parte de responsables vacía, por ejemplo, se guarda 'Tú' en este " +
                "campo, y esto sirve para identificar los pendientes en donde solo tú eres el responsable.");

        sb.append("\n\n").append("También, es relevante mencionar que la prioridad 10 solo se pone " +
                "cuando dejas la opción default en la parte de prioridad, y que cuando no se le pone" +
                " fecha de entrega a una tarea, esta se mueve al final cuando se ordenan las tareas por" +
                " fecha.");

        sb.append("\n\n").append("En esta aplicación, la prioridad 1 se considera como la más alta " +
                "y la 10 como la más baja o menos importante.");

        textBody.setText(sb.toString());
    }
}