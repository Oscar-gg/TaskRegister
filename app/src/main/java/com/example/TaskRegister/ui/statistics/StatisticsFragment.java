package com.example.TaskRegister.ui.statistics;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.TaskRegister.MainActivity;
import com.example.TaskRegister.Pendientes;
import com.example.TaskRegister.R;
import com.example.TaskRegister.ui.filter.OrdenarPendientes;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

// El propósito de esta clase es desplegar estadísticas básicas que le den información general al
// usuario, como el número de pendientes completos o la cantidad de pendientes atrasados. Esta
// clase es útil para checar si hay errores en el programa.

// Las líneas que tienen *Android Def o <Android Def> </Android Def> son las que fueron generadas
// por Android Studio. Total: 5 líneas aproximadamente.

public class StatisticsFragment extends Fragment {

    // <Android Def>
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_statistics, container, false);
    }


    public void onResume(){
        super.onResume();
        // </Android Def>
        initializeTextViews(getView());
    }

    private void initializeTextViews(View view) {
        TextView totalTasks, activeTasks, completedTasks, overDueTasks;
        totalTasks = view.findViewById(R.id.statistics_totalTasks);
        activeTasks = view.findViewById(R.id.statistics_activeTasks);
        completedTasks = view.findViewById(R.id.statistics_completedTasks);
        overDueTasks = view.findViewById(R.id.statistics_overDueTasks);

        String strTotalTasks = Integer.toString(Pendientes.getActiveTasks().size() +
                                                   Pendientes.getCompletedTasks().size());
        String strActiveTasks = Integer.toString(Pendientes.getActiveTasks().size());
        String strCompletedTasks = Integer.toString(Pendientes.getCompletedTasks().size());

        totalTasks.setText(strTotalTasks);
        activeTasks.setText(strActiveTasks);
        completedTasks.setText(strCompletedTasks);
        overDueTasks.setText(getOverdue());

    }
    // Regresa el numero de pendientes atrasados.
    private String getOverdue() {
        int tasksOverDue = 0;

        // Valor númerico del día actual.
        int currentDay = OrdenarPendientes.numericDate(MainActivity.getDay());

        Set entrySet = Pendientes.getActiveTasks().entrySet();
        Iterator it = entrySet.iterator();

        Map.Entry<Integer, Pendientes> entry;

        while(it.hasNext()){
            entry = (Map.Entry<Integer, Pendientes>) it.next();
            int day = OrdenarPendientes.numericDate(entry.getValue().getDueDate());
            if (day < currentDay){
                tasksOverDue++;
            }
        }

        return Integer.toString(tasksOverDue);
    }
}