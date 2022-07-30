package com.example.TaskRegister.ui.fourth_fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.TaskRegister.ListAdapter;
import com.example.TaskRegister.ListElement;
import com.example.TaskRegister.MainActivity;
import com.example.TaskRegister.Pendientes;
import com.example.TaskRegister.R;
import com.example.TaskRegister.RegisterData;
import com.example.TaskRegister.ui.filter.OrdenarPendientes;

import java.util.List;

// El propósito de esta clase es desplegar los pendientes que han sido terminados por el usuario, y
// que el usuario pueda buscar pendientes específicos de manera sencilla.

// Las líneas que tienen *Android Def o <Android Def> </Android Def> son las que fueron generadas
// por Android Studio. Total: 5 líneas aproximadamente.

/*
Importante: el código adaptado de fuentes de internet es el siguiente:

RecyclerView OnClickListener [https://www.youtube.com/watch?v=69C1ljfDvl0]: líneas encerradas por

<RVCL> </RVCL>. Si solo tiene *RVCL significa que fue una sola línea. Total: 3 líneas.

RecyclerView con CardView [https://www.youtube.com/watch?v=HrZgfoBeams]: líneas marcadas con
<RVCV> </RVCV>. Si solo tiene *RVCV significa que fue una sola línea. Total: 8 líneas.
*/

public class FinishedTasksFragment extends Fragment implements ListAdapter.OnTaskClickListener {

    private static List<ListElement> elements; // Contiene la información a mostrar. *RVCV
    private RecyclerView tasksRecyclerView; // *RVCV
    // Usado para no tener que cargar el recyclerView si no hubo cambios
    private static boolean changesMade = true;
    public static boolean usingFilters = false; // Se modifica cuando se llama el método para los filtros.
    // Spinners donde se muestran las opciones para filtrar
    private Spinner filters, filterOptions;

    // <Android Def>
    // Se llama cuando se diseña el fragmento por primera vez.
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        // </Android Def>
        tasksRecyclerView = view.findViewById(R.id.home_rv_activeTasks);
        filters = view.findViewById(R.id.spinner);
        filterOptions = view.findViewById(R.id.spinner2);
        return view;
    }

    // Se llama después de onCreateView, incluso la primera vez que se muestra el fragmento.
    @Override
    public void onResume() { // *Android Def
        super.onResume(); // *Android Def
        loadRecyclerView();
        loadSpinners();
    }

    // Carga los spinners, estos modifican el recyclerView cuando hay una selección por el usuario.
    private void loadSpinners() {
        // Se usa otro arreglo porque no hace sentido filtrar por estatus en la parte de pendientes
        // terminados.
        String[] options = {"Elegir", "Categoría", "Prioridad", "Responsables"};

        // El primer argumento es el contexto, el segundo el estilo del spinner, y el tercero es la
        // lista a desplegar.
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(getActivity(),
                R.layout.spinner_item_design, options);
        filters.setAdapter(adapter1);
        filterOptions.setVisibility(View.GONE); // Hace que sea invisible el segundo spinner hasta
        // que ponga algo
        spinner1Actions();
        spinner2Actions();
    }

    // Las acciones del spinner 1 (El spinner que tiene las opciones del menu)
    private void spinner1Actions() {
        filters.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Se consigue la seleccion del spinner.
                String selection = (String) parent.getItemAtPosition(position);

                // Se consiguen las opciones a mostrar a partir de la selección
                String[] filterOptionsStrings = OrdenarPendientes.optionsToShow(selection);

                // Regresa null cuando no se selecciona nada o cuando se selecciona la fecha
                if (filterOptionsStrings != null){
                    // Se hace visible el segundo spinner si se seleccionó algo.
                    filterOptions.setVisibility(View.VISIBLE);
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                            R.layout.spinner_item_design, filterOptionsStrings);
                    filterOptions.setAdapter(adapter); // Se le ponen las opciones al spinner.
                } else {
                    filterOptions.setVisibility(View.GONE);
                    changesMade = true;
                    usingFilters = false;
                    loadRecyclerView();
                }

            }
            // Este método se llama cuando se remueve la vista, entonces no lo implemento.
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    // Las acciones del spinner 2 (El spinner que tiene las opciones de filtro)
    private void spinner2Actions() {

        // Se le pone el listener al segundo spinner, para que detecte cuando el usuario cambia de
        // opción.
        filterOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Se consigue la seleccion del spinner.
                String selection = (String) parent.getItemAtPosition(position);
                String menuSelected = filters.getSelectedItem().toString();
                // Se checa si no esta seleccionado el default.
                if (!selection.equals("Seleccionar")){
                    usingFilters = true;

                    // Hace que los pendientes que tengan la selección sean mostrados. Estos pendientes
                    // son adicionalmente ordenados por prioridad y fecha, en el orden que el
                    // usuario indique.
                    OrdenarPendientes.filterBy(1, selection, menuSelected);

                } else {
                    // Se cargan los pendientes ordenados por prioridad y fecha.
                    usingFilters = false;
                }
                changesMade = true;
                loadRecyclerView();

            }
            // Este método se llama cuando se remueve la vista, entonces no lo implemento.
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    // Carga el recyclerview (La parte que contiene la información de los pendientes).
    private void loadRecyclerView() {
        if (changesMade){
            initializeElements(); // Hace la lista de elementos
            changesMade = false;
        }
        //*RVCL
        ListAdapter listAdapter = new ListAdapter(elements, getContext(), this);

        // <RVCV>
        tasksRecyclerView.setHasFixedSize(true);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        tasksRecyclerView.setAdapter(listAdapter);
        // </RVCV>
    }

    // Este método se tiene que modificar para la parte de los filtros.
    private void initializeElements() {
        elements = new ArrayList<>(); // *RVCV

        if (!usingFilters) {
            // El id (arg 1) indica que son los pendientes inactivos. El true indica que si hay
            // opciones de antes se tienen que resetear.
            OrdenarPendientes.finalSorts(1, true);
        }

        int[] sorted = OrdenarPendientes.getFilteredList();

        Pendientes a;
        for (int x = 0; x<sorted.length; x++){
            a = Pendientes.getCompletedTasks().get(sorted[x]);
            elements.add(new ListElement(a.getName(), a.getDueDate(), a.getCategory(), // *RVCV
                    Integer.toString(a.getTaskID()), a.getStatus(), a.getPriority()) );
        }
    }

    // Ejecuta una actividad nueva, con la información del pendiente seleccionado.
    @Override
    public void onTaskClick(int position) { //*RVCL
        Intent intent = new Intent(getContext(), RegisterData.class);
        // Se manda el id de la tarea para acceder a ella desde la otra actividad.
        intent.putExtra("action", elements.get(position).getTaskID());
        startActivity(intent);
    }

    // Mueve el pendiente de completados a activos. Se ejecuta cuando es presionado un botón de los
    // pendientes.
    @Override
    public void onButtonClick(int position) {
        // Primero se consigue el id del pendiente que fue presionado
        int taskIndex = Integer.parseInt(elements.get(position).getTaskID());
        // Se cambia su status, para que concuerde y se actualiza la fecha de modificación.
        Pendientes task = Pendientes.getCompletedTasks().get(taskIndex);
        task.setStatus("-", MainActivity.getDay());
        // Después, se cambia de lista el pendiente.
        Pendientes.toggleCompleted(task);

        // Finalmente, se resetea el recyclerView. El reseteo se hace de esta manera para quitar las
        // selecciones de los filtros en caso de que haya.

        if (filters.getSelectedItem().equals("Elegir")){
            changesMade = true;
            loadRecyclerView();
        } else {
            filters.setSelection(0);
        }
        Toast.makeText(getContext(), "El pendiente fue devuelto a la lista de activos.", Toast.LENGTH_SHORT).show();
    }

    // Getters y setters
    public static boolean isChangesMade() {
        return changesMade;
    }

    public static void setChangesMade(boolean changesMade) {
        FinishedTasksFragment.changesMade = changesMade;
    }

}