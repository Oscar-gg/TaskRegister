package com.example.TaskRegister;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.TaskRegister.ui.filter.OrdenarPendientes;

// En esta clase se guardan los ajustes del usuario, o sus preferencias. Por el momento solo se
// puede cambiar la preferencia para ordenar pendientes, para indicar que se pongan las tareas en
// cierto color con base en el estatus, y para indicar que se quieren los botones (de las tareas) en
// cierto color con base en la prioridad.

// Las líneas que tienen *Android Def o <Android Def> </Android Def> son las que fueron generadas
// por Android Studio. Total: 3 líneas aproximadamente.

public class SettingsActivity extends AppCompatActivity {

    private SwitchCompat switchUrgency, switchColorTasks, switchColorButtons, switchConfirm,
                         switchCompactView;

    // <Android Def>
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
    // </Android Def>

        // Le pone el título a la barra superior de la pantalla.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getResources().getString(R.string.action_settings));
        }
        initializeButton();
        // Switch 1 = para seleccionar prioridad o urgencia. Switch 2 = colorear tareas,
        // Switch 3 = colorear botones.
        switchUrgency = findViewById(R.id.settings_switch);
        switchColorTasks = findViewById(R.id.settings_switch2);
        switchColorButtons = findViewById(R.id.settings_switch3);
        switchConfirm = findViewById(R.id.settings_switch4);
        switchCompactView = findViewById(R.id.settings_switch5);


        // Pone el switch conforme a la información guardada.

        switchColorTasks.setChecked(MainActivity.isPaintTasks());
        switchColorButtons.setChecked(MainActivity.isPaintButtons());
        switchConfirm.setChecked(MainActivity.isConfirmCompleted());
        switchCompactView.setChecked(MainActivity.isCompactView());

        // Nota: se le pone lo contrario a la selección porque el botón de switch es para indicar
        // si la prioridad es más importante y lo que registra el programa es si la urgencia es más
        // importante.
        switchUrgency.setChecked(!OrdenarPendientes.isUrgencySelected());

    }

    // Le pone el listener al botón. Guarda la información que esté seleccionada.
    private void initializeButton() {

        Button button = findViewById(R.id.settings_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getSharedPreferences("settings", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit(); // Permite editar preferencias
                // Le pone el valor seleccionado. El primer argumento es el nombre donde se guarda
                // el valor. Estos tienen que concordar con los utilizados para acceder a las
                // preferencias en MainActivity.
                editor.putBoolean("urgency", !switchUrgency.isChecked());
                editor.putBoolean("task_color", switchColorTasks.isChecked());
                editor.putBoolean("btn_color", switchColorButtons.isChecked());
                editor.putBoolean("confirm_completed", switchConfirm.isChecked());
                editor.putBoolean("compact_view", switchCompactView.isChecked());
                editor.apply(); // Guarda los cambios.

                // Aplica los cambios en el programa.
                OrdenarPendientes.setUrgencySelected(switchUrgency.isChecked());
                MainActivity.setPaintTasks(switchColorTasks.isChecked());
                MainActivity.setPaintButtons(switchColorButtons.isChecked());
                MainActivity.setConfirmCompleted(switchConfirm.isChecked());
                MainActivity.setCompactView(switchCompactView.isChecked());


                // Manda un mensaje al usuario indicando los cambios.
                Toast.makeText(v.getContext(), "Se guardaron los cambios", Toast.LENGTH_SHORT).show();
            }
        });

    }


}