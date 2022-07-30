package com.example.TaskRegister;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.TaskRegister.ui.filter.OrdenarPendientes;
import com.example.TaskRegister.ui.fourth_fragment.FinishedTasksFragment;
import com.example.TaskRegister.ui.home.HomeFragment;

import org.jetbrains.annotations.NotNull;

// Esta clase es utilizada para registrar y guardar la información que ingresa el usuario. Tiene
// componentes que procuran facilitar la entrada de datos, y también hay validación de estos.

// Las líneas que tienen *Android Def o <Android Def> </Android Def> son las que fueron generadas
// por Android Studio. Total: 3 líneas aproximadamente.

public class RegisterData extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    // Son los componentes que están para registrar la información.
    private EditText name, description, category, responsibles;
    private TextView creationDate, lastUpdateDate, dateSelected;
    private Button btnUpdate, btnDelete, btnSave;
    private Spinner priority, statusOptions, categoryOptions, addResponsible;
    private int selectedTask;
    private static Pendientes currentTask;//Se usa en caso de que se vaya a actualizar el pendiente.
    // Se usa para separar las lineas en la descripción.
    private static final String descriptionSeparator = "@newDescriptionLine@";
    private AlertDialog currentDialog;

    // <Android Def>
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_data);
        // </Android Def>

        // Conexión entre los objetos EditText y la parte lógica. Ahora se puede acceder a los
        // valores que ponga el usuario.
        name = (EditText) findViewById(R.id.register_taskName);
        description = (EditText) findViewById(R.id.register_description);
        category = (EditText) findViewById(R.id.register_category);
        dateSelected = (TextView) findViewById(R.id.register_tv10);
        responsibles = (EditText) findViewById(R.id.register_responsibles);

        // Conexión entre los objetos Spinner y la parte lógica.
        priority = (Spinner) findViewById(R.id.register_spinner_priority);
        statusOptions = (Spinner) findViewById(R.id.register_spinner_status);
        categoryOptions = (Spinner) findViewById(R.id.register_spinner_category);
        addResponsible = (Spinner) findViewById(R.id.register_add_responsible);

        initializeButtons();
        initializeSpinners();

        // Checa si la actividad fue llamada para agregar un pendiente, o para modificar uno.
        // Muestra ciertos botones dependiendo de la selección del usuario.

        String action = getIntent().getStringExtra("action");
        int taskNumber;

        // Le pone un título a la barra de arriba, dependiendo de la selección del usuario.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            if (action.equals("RegisterTask")){
                actionBar.setTitle("Crear Pendiente");
            } else {
                actionBar.setTitle("Modificar/borrar Pendiente");
            }
        }

        if (action.equals("RegisterTask")){
            Toast.makeText(this, "Ingrese los datos de la nueva tarea",
                    Toast.LENGTH_SHORT).show();
        } else {
            try{
                taskNumber = Integer.parseInt(action);
                selectedTask = taskNumber;
            } catch(Exception e){
                Toast.makeText(this, "Occurió un error.", Toast.LENGTH_SHORT).show();
            }
            registerChanges();
        }

    }

    // Crea algunos botones de la pantalla.
    private void initializeButtons() {
        ImageButton calendarOpen = (ImageButton) findViewById(R.id.register_calendar_open);
        // Abre el calendario cuando se presiona el botón.
        calendarOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        Button removeDate = (Button) findViewById(R.id.register_date_remove);
        // Permite que el usuario no coloque fecha de entrega
        removeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateSelected.setText("-");
            }
        });

        Button removeResponsible = (Button) findViewById(R.id.register_remove_responsibles);
        removeResponsible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                responsibles.setText(""); // Borra todos los responsables.
            }
        });

    }

    public void onPause() {
        super.onPause();
        // Hace que se quite el diálogo de confirmación del botón cuando se rota la pantalla.
        if (currentDialog != null)
            currentDialog.dismiss();
    }

    // Se agregan las opciones a los spinners.
    private void initializeSpinners() {
        // Spinner de prioridades
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this,
                R.layout.spinner_item_design, Pendientes.getOptionsPriority());
        // Le pone el arreglo de opciones al spinner de prioridad.
        priority.setAdapter(adapter1);

        // Spinner de status.
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this,
                R.layout.spinner_item_design, Pendientes.getStatusOptions());
        // Le pone el arreglo de opciones al spinner de status.
        statusOptions.setAdapter(adapter2);

        // Este se hace en otro método porque tendrá un listener y funcionará diferente, ya que para
        // las categorías el usuario puede ingresar una categoría con spinner o escribir una nueva.
        initializeCategorySpinner();
        initializeResponsibleSpinner();
    }

    // Configura el spinner de los responsables.
    private void initializeResponsibleSpinner() {
        String[] responablesActuales = OrdenarPendientes.getResponsiblesAvailable();
        String[] displayResponsible = new String[responablesActuales.length];
        displayResponsible[0] = "-";

        if (responablesActuales.length - 1 >= 0)
            System.arraycopy(responablesActuales, 1, displayResponsible, 1, responablesActuales.length - 1);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.spinner_item_design, displayResponsible);
        addResponsible.setAdapter(adapter);
        addResponsible.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!selection.equals("-")){
                    // Se checa que la opción no haya sido puesta con anterioridad.
                    boolean appears = false;
                    String[] responsablesTemp;
                    if (responsibles!= null){
                        responsablesTemp =
                                responsibles.getText().toString().split(",");
                        for (String responsable: responsablesTemp){
                            if (selection.equals(responsable.trim())){
                                appears = true;
                                break;
                            }
                        }
                    }

                    if (appears){
                        Toast.makeText(parent.getContext(), "Ese responsable ya está en la lista", Toast.LENGTH_SHORT).show();
                    } else {
                        if (responsibles!= null){
                            String responsables = responsibles.getText().toString();
                            if (responsables.length() == 0){
                                responsibles.setText(selection);
                            } else {
                                responsibles.setText(responsables + ", " + selection);
                            }
                        } else{
                            responsibles.setText(selection);
                        }

                    }
                    addResponsible.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void initializeCategorySpinner() {
        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(this,
                R.layout.spinner_item_design, OrdenarPendientes.getCategoriesAvailable());
        // Le pone el arreglo de opciones al spinner de categorias.
        categoryOptions.setAdapter(adapter3);
        categoryOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                // Si el usuario elige una opción
                if (!selection.equals("Seleccionar")){
                    category.setText(selection); // Le pone la selección al TextView
                    categoryOptions.setSelection(0); // Vuelve a poner la opción de seleccionar.
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    // Los cambios que hay cuando el usuario quiere modificar un pendiente en lugar de crearlo.
    // En general, algunos botones desaparecen, otros aparecen, y se carga la información pasada
    // del pendiente.
    private void registerChanges() {
        loadTaskPreviousInformation();
        loadBottons();
        loadTextViews();
    }

    // Añade textViews que solamente deberían estar cuando se va a modificar el pendiente.
    private void loadTextViews() {
        // Conexion con la parte grafica y se enciende la visibilidad.
        creationDate = (TextView) findViewById(R.id.register_tv8);
        lastUpdateDate = (TextView) findViewById(R.id.register_tv9);
        creationDate.setVisibility(View.VISIBLE);
        lastUpdateDate.setVisibility(View.VISIBLE);
        creationDate.setText("Fecha de creación: " + currentTask.getDateCreated());
        lastUpdateDate.setText("Última modificación: " + currentTask.getLastEdit());

    }

    // Añade los botones que solamente deben estar cuando se quiere modificar un pendiente.
    private void loadBottons() {
        btnUpdate = (Button) findViewById(R.id.register_btn_update);
        btnDelete = (Button) findViewById(R.id.register_btn_delete);
        btnSave = (Button) findViewById(R.id.register_btn_save);

        btnUpdate.setVisibility(View.VISIBLE);
        btnDelete.setVisibility(View.VISIBLE);
        btnSave.setVisibility(View.GONE);
    }

    // Método cuando se pulsa el botón "Borrar"
    public void onClickBtnDelete(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterData.this);

        builder.setTitle(R.string.dialog_erase_title);
        builder.setMessage(R.string.dialog_erase_message);

        // Botón para confirmar que se quiere borrar el pendiente.
        builder.setPositiveButton(R.string.string_btn1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Pendientes.deleteTask(currentTask);
                HomeFragment.setChangesMade(true);
                FinishedTasksFragment.setChangesMade(true);
                finish();
            }
        });

        // Botón para cancelar.
        builder.setNegativeButton(R.string.string_btn2, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        currentDialog = builder.create(); // Se crea el diálogo.
        currentDialog.show();             // Se muestra el diálogo.
    }

    public void onClickBtnUpdate(View view){
        if(checkInformation()){
            Pendientes taskUpdate = loadNewPendiente();
            taskUpdate.setDateCreated(currentTask.getDateCreated());
            taskUpdate.setTaskID(currentTask.getTaskID());
            taskUpdate.setLastEdit(MainActivity.getDay());

            // A final de cuentas, estas lineas de código hacen que se incremente el número id
            // de los pendientes. Esto no es un problema mientras que cada pendiente siga almacenado
            // en una posicion que sea equivalente a su id. Y, como se resetean los id cada vez que
            // se abre la aplicación, no hay que preocuparse de que se hagan números muy grandes.
            if (currentTask.getCompleted()){
                Pendientes.getCompletedTasks().remove(currentTask.getTaskID());
            } else {
                Pendientes.getActiveTasks().remove(currentTask.getTaskID());
            }
            if (taskUpdate.getCompleted()){
                Pendientes.getCompletedTasks().put(selectedTask, taskUpdate);
            } else {
                Pendientes.getActiveTasks().put(selectedTask, taskUpdate);
            }
            HomeFragment.setChangesMade(true);
            FinishedTasksFragment.setChangesMade(true);
            finish();
        }

    }

    // Carga la información del pendiente a la pantalla.
    private void loadTaskPreviousInformation() {
        // Se hace un apuntador con nombre corto de la variable.
        currentTask = Pendientes.getActiveTasks().get(selectedTask);
        try{
            // Si no existe el pendiente, significa que es un pendiente completado.
            name.setText(currentTask.getName());
        } catch(Exception e){
            currentTask = Pendientes.getCompletedTasks().get(selectedTask);
            name.setText(currentTask.getName());
        }

        String[] descriptionLines = currentTask.getDescription().split(descriptionSeparator);

        if (descriptionLines.length > 1){
            //Significa que si había varias líneas.
            description.setText(processDescription(descriptionLines));
        } else {
            // Se ejecuta si el usuario no había puesto líneas de más con anterioridad.
            description.setText(currentTask.getDescription());
        }


        category.setText(currentTask.getCategory());
        dateSelected.setText(currentTask.getDueDate());

        String responsiblesLine = "";
        for (int x = 0; x<currentTask.getResponsible().length; x++){
            responsiblesLine += currentTask.getResponsible()[x] + ", ";
        }
        // Se hace para quitar el útlimo ", ".
        responsiblesLine = responsiblesLine.substring(0,responsiblesLine.length()-2);
        responsibles.setText(responsiblesLine);

        for (int x = 0; x<Pendientes.getStatusOptions().length;x++){
            if (currentTask.getStatus().equals(Pendientes.getStatusOptions()[x])){
                statusOptions.setSelection(x);
                break;
            }
        }

        for (int x = 0; x< OrdenarPendientes.getPrioritiesAvailable().length; x++){
            // Se hace esto porque el spinner no tiene el número 10 como opción
            if (currentTask.getPriority() == 10){
                priority.setSelection(0);
                break;
            }
            if (Integer.toString(currentTask.getPriority()).equals(
                                            OrdenarPendientes.getPrioritiesAvailable()[x])){
                priority.setSelection(x);
                break;
            }
        }

    }

    // Metodo que se llama al hacer click al botón. Finaliza la actividad y crea el objeto.

    public void registerInformation(View view){
        if (checkInformation()){
            Pendientes taskNew = loadNewPendiente();
            Pendientes.addTask(taskNew);    // Añade el pendiente al HashMap correspondiente
            HomeFragment.setChangesMade(true);
            FinishedTasksFragment.setChangesMade(true);
            finish();
        }
    }

    @NotNull
    private Pendientes loadNewPendiente() {
        String name, description, status, category, date;
        String[] responsibles;
        int priority;
        try {
            priority = Integer.parseInt(this.priority.getSelectedItem().toString());
            // El único error puede ser que esté seleccioando el string ##.
        } catch (Exception e){
            priority = 10;
        }

        // Conseguir los datos del tipo necesario para el constructor
        name = this.name.getText().toString();
        status = this.statusOptions.getSelectedItem().toString();
        category = this.category.getText().toString();
        date = this.dateSelected.getText().toString();

        description = this.description.getText().toString();
        String[] descriptionLines = description.split("\n");

        if (descriptionLines.length > 1){
            // Significa que el usuario puso varios enter. Esto se tiene que modificar porque la
            // información es guardada en un archivo de texto, y cada pendiente va separado por una
            // línea o "\n".
            description = handleDescription(descriptionLines);
            // Esto solo se hace con la descripción porque no se pueden poner lineas extra en los
            // demás apartados.
        }

        // Ponen la fecha en formato dd/mm/yy, en caso de que el usuario haya puesto un mes o un
        // día de un digito.
        if (date.length() != 0 && !(date.equals("-"))){
            if(date.length() != 8){
                String[] formatDate = date.split("/");
                if (formatDate[0].length() == 1){
                    date = "0" + date;
                }
                if (formatDate[1].length() == 1){
                    date = date.substring(0,3) + "0" + date.substring(3);
                }
            }
        }

        // Pone los responsables en formato correcto.
        responsibles = this.responsibles.getText().toString().split(",");
        if (responsibles.length == 1){
            if (responsibles[0].equals("")){
                responsibles = null;
            }
        } else {
            // Quita los espacios en blanco extras.
            for (int x = 0; x < responsibles.length; x++){
                responsibles[x] = responsibles[x].trim();
            }
        }

        return new Pendientes(name, description, status, priority, responsibles,
                date, category, MainActivity.getDay());
    }

    // Método que quita los \n a la descripción. En lugar de eso pone una string. La string que es
    // añadida será remplazada por "\n" cuando se cargue la información.
    private static String handleDescription(String[] descriptionLines) {
        String descriptionLine = "";

        for (String line : descriptionLines) {
            descriptionLine += line + descriptionSeparator;
        }

        // Le quita el ultimo descriptionSeparator.
        return descriptionLine.substring(0, descriptionLine.length() - descriptionSeparator.length());
    }

    // Método que devuelve la descripción modificada a la original. Hace que tenga varias lineas.
    private static String processDescription(String[] descriptionLines) {
        String originalDescription = "";

        for (String line : descriptionLines) {
            originalDescription += line + "\n";
        }

        // Le quita el último "\n".
        return originalDescription.substring(0, originalDescription.length()-1);
    }

    // Checa que toda la información este bien, si hay algo mal envia mensajes al usuario. Solo se
    // checa si hay nombre y si la fecha es válida.
    private boolean checkInformation(){

        // Para recuperar texto de un componente en string se usa: objeto.getText().toString()
        if (name.getText().toString().equals("")){
            Toast.makeText(this, "Tienes que ingresar un nombre", Toast.LENGTH_SHORT)
                    .show(); // El show() hace que se muestre el mensaje.
            return false;
        }

        String dateString = dateSelected.getText().toString();


        if (dateString.equals("DD/MM/YY") || dateString.equals("-")){
            dateSelected.setText("");
            return true; // Significa que no hay ninguna fecha
        }

        if (dateString.length() > 8){
            Toast.makeText(this, "La fecha de entrega tiene números de más", Toast.LENGTH_SHORT)
                    .show();
            return false;
        } else if(dateString.length() < 6){
            Toast.makeText(this, "Le faltan números a la fecha de entrega", Toast.LENGTH_SHORT)
                    .show();
            return false;
        }

        // A partir de aquí se checa que la fecha sea válida.

        String[] dateSeparated = dateString.split("/"); // [0] = dia, [1] = mes, [2] = año

        if (dateSeparated.length != 3){
            Toast.makeText(this, "Ingrese una fecha válida.", Toast.LENGTH_SHORT)
                    .show();
            return false;
        }

        int day, month, year;

        try {
            day = Integer.parseInt(dateSeparated[0]);
            month = Integer.parseInt(dateSeparated[1]);
            year = Integer.parseInt(dateSeparated[2]);
        } catch(Exception e){
            Toast.makeText(this, "Ingrese una fecha válida.", Toast.LENGTH_SHORT)
                    .show();
            return false;
        }

        // A partir de aquí solo se checa que la fecha sea igual o mayor a la actual.

        if (statusOptions.getSelectedItem().toString().equals("Terminado")){
            return true;
        }

        if (day < 0 || day > 31){
            Toast.makeText(this, "Ese día no existe.", Toast.LENGTH_SHORT)
                    .show();
            return false;
        }

        if (month <= 0 || month > 12){
            Toast.makeText(this, "Ese mes no existe.", Toast.LENGTH_SHORT)
                    .show();
            return false;
        }
        if (year < 0 || year > 100){
            Toast.makeText(this, "Vuelva a ingresar el año.", Toast.LENGTH_SHORT)
                    .show();
            return false;
        }

        String currentDay = MainActivity.getDay();
        String[] currentDaySeparated = currentDay.split("/");

        int currentMonthDay = Integer.parseInt(currentDaySeparated[0]);
        int currentMonth = Integer.parseInt(currentDaySeparated[1]);
        int currentYear = Integer.parseInt(currentDaySeparated[2]);

        if(year < currentYear){
            Toast.makeText(this, "El año no puede ser menor que " + currentYear, Toast.LENGTH_SHORT)
                    .show();
            return false;
        }

        if (year == currentYear){
            if (month < currentMonth){
                Toast.makeText(this, "Esa fecha ya pasó. Ingrese otra.", Toast.LENGTH_SHORT)
                        .show();
                return false;
            }
            if (month == currentMonth){
                if (day < currentMonthDay){
                    Toast.makeText(this, "Esa día ya pasó. Ingrese otro.", Toast.LENGTH_SHORT)
                            .show();
                    return false;
                }
            }
        }

        return true;
    }

    // Pone la fecha del día actual si se le da click al botón que dice "hoy".
    public void buttonToday(View view){
        dateSelected.setText(MainActivity.getDay());
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String date = dayOfMonth + "/" + (month + 1) + "/" + (Integer.toString(year).substring(2));
        dateSelected.setText(date);
    }
}