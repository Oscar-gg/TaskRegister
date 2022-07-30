package com.example.TaskRegister;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.TaskRegister.ui.filter.OrdenarPendientes;

import java.util.List;

/*
Importante: el código adaptado de fuentes de internet es el siguiente:

RecyclerView OnClickListener [https://www.youtube.com/watch?v=69C1ljfDvl0]: líneas encerradas por

<RVCL> </RVCL>. Si solo tiene *RVCL significa que fue una sola línea. Total: 11 líneas.

RecyclerView con CardView [https://www.youtube.com/watch?v=HrZgfoBeams]: líneas marcadas con
<RVCV> </RVCV>. Si solo tiene *RVCV significa que fue una sola línea. Total: 16 líneas aprox.

Animación para CardView [https://www.youtube.com/watch?v=Sp1XkjaI4u0]: líneas marcadas con *CVA.
Total: 3 líneas.

Nota: Muchos de los métodos se tienen que implementar tal cual para poder utilizar el
recyclerView.
*/


// Relaciona lo gráfico (establecido en list_element.xml) con los datos.
public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    // <RVCV>
    private List<ListElement> mData;
    private LayoutInflater mInflater;
    private Context context;
    // </RVCV>
    private OnTaskClickListener mOnTaskClickListener; // *RVCL

         // <RVCV>
    public ListAdapter(List<ListElement> itemList, Context context, OnTaskClickListener OnTaskClickListener){
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mData = itemList;
        // </RVCV>
        this.mOnTaskClickListener = OnTaskClickListener; // *RVCL
    }

    // <RVCV>
    @Override
    public int getItemCount(){ return mData.size();}

    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        // Se vincula el aspecto de la lista con el elaborado en el xml.
        View view;

        if (MainActivity.isCompactView()){
            // Vista compacta
            view = mInflater.inflate(R.layout.compact_list_element, parent, false);

        } else {
            // Vista expandida
             view = mInflater.inflate(R.layout.list_element, null);
        }

        return new ListAdapter.ViewHolder(view, mOnTaskClickListener); // *RVCL
    }

    @Override
    public void onBindViewHolder(final ListAdapter.ViewHolder holder, final int position){
        // *CVA
        holder.cardView.setAnimation(AnimationUtils.loadAnimation(context, R.anim.card_view_animation));
        holder.bindData(mData.get(position));
    }

    public void setItems(List<ListElement> items){ mData = items;}
    // </RVCV>

    // El ViewHolder contiene todos los componentes que se muestran por cada pendiente.
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Estos son los componentes que tiene cada tarea. Dichos componentes fueron establecidos
        // en list_element.xml
        Button completed;
        TextView taskName, dueDate, category, priorityTop, priorityBottom;
        int priority;
        String status;
        LinearLayout layout;
        CardView cardView;  // *CVA

        OnTaskClickListener onTaskClickListener; // *RVCL

        ViewHolder(View itemView, OnTaskClickListener onTaskClickListener){ // *RVCV
            super(itemView);                                                // *RVCV

            // Se vinculan las variables con los componentes de la IU.
            if (!MainActivity.isCompactView()){
                linkAllComponents();
            } else {
                linkCompactComponents();
            }


            this.onTaskClickListener = onTaskClickListener; // *RVCL

            // Se le pone el listener al botón de completados. (El botón de la izquierda)
            completed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Cuando se le hace clic al botón, se ejecuta el método de la interface y se
                    // le pasa como argumento la posición del pendiente que fue presionado.
                    onTaskClickListener.onButtonClick(getAdapterPosition()); // *RVCL
                }
            });

            // Esto le agrega el listener al viewGroup completo. No se ejecuta cuando se presiona
            // el botón.
            itemView.setOnClickListener(this); // *RVCL

        }

        // Vincula los elementos de la vista compacta.
        private void linkCompactComponents() {
            completed = itemView.findViewById(R.id.buttonSubmit);
            taskName = itemView.findViewById(R.id.taskName);
            dueDate = itemView.findViewById(R.id.dueDate_textDisplay);
            layout = itemView.findViewById(R.id.itemLayout);
            cardView = itemView.findViewById(R.id.cardView); // *CVA
            priorityBottom = itemView.findViewById(R.id.tv_priority_color);
        }

        // Se vinculan todos los elementos de la cardview. Esto sucede cuando está desactivada la
        // vista compacta.
        private void linkAllComponents() {
            completed = itemView.findViewById(R.id.buttonSubmit);
            taskName = itemView.findViewById(R.id.taskName);
            dueDate = itemView.findViewById(R.id.dueDate_textDisplay);
            category = itemView.findViewById(R.id.card_view_category);
            layout = itemView.findViewById(R.id.itemLayout);
            cardView = itemView.findViewById(R.id.cardView); // *CVA
            priorityTop = itemView.findViewById(R.id.tv_priority_color_2);
            priorityBottom = itemView.findViewById(R.id.tv_priority_color);
        }

        // Le pone color de fondo al texto con base en el estatus de la tarea.
        private void setStatusColor(String status) {

            if (status == null){
                layout.setBackgroundColor(itemView.getContext().getResources()
                        .getColor(R.color.white));
                return;
            }

            switch(status){
                case "Por iniciar":
                    layout.setBackgroundColor(itemView.getContext().getResources()
                            .getColor(R.color.status_start));
                    break;
                case "En progreso":
                    layout.setBackgroundColor(itemView.getContext().getResources()
                            .getColor(R.color.status_in_progress));
                    break;
                case "En espera":
                    layout.setBackgroundColor(itemView.getContext().getResources()
                            .getColor(R.color.status_on_hold));
                    break;
                case "Terminado":
                    layout.setBackgroundColor(itemView.getContext().getResources()
                            .getColor(R.color.status_finished));
                    break;
                default:
                    layout.setBackgroundColor(itemView.getContext().getResources()
                            .getColor(R.color.white));
                    break;
            }
        }

        // Le pone el color a los bordes de la tarjeta, dependiendo de la prioridad seleccionada.
        // Los colores fueron asignados desde el archivo colors.xml. res>values>colors.xml
        // Se checa que los valores no sean nulos porque algunas plantillas
        // pueden no tener algunos elementos.
        private void setBorderColors(int priority) {
            switch(priority){
                case 1:
                    if (priorityTop!= null){
                        priorityTop.setBackgroundColor(itemView.getContext().getResources()
                                .getColor(R.color.btnpriority1));
                    }
                    if (priorityBottom != null){
                        priorityBottom.setBackgroundColor(itemView.getContext().getResources()
                                .getColor(R.color.btnpriority1));
                    }

                    break;
                case 2:
                    if (priorityTop!= null){
                        priorityTop.setBackgroundColor(itemView.getContext().getResources()
                                .getColor(R.color.btnpriority2));
                    }
                    if (priorityBottom!=null){
                        priorityBottom.setBackgroundColor(itemView.getContext().getResources()
                                .getColor(R.color.btnpriority2));
                    }

                    break;
                case 3:
                    if (priorityTop!= null){
                        priorityTop.setBackgroundColor(itemView.getContext().getResources()
                                .getColor(R.color.btnpriority3));
                    }
                    if (priorityBottom != null){
                        priorityBottom.setBackgroundColor(itemView.getContext().getResources()
                                .getColor(R.color.btnpriority3));
                    }

                    break;
                case 4:
                    if (priorityTop!= null){
                        priorityTop.setBackgroundColor(itemView.getContext().getResources()
                                .getColor(R.color.btnpriority4));
                    }
                    if (priorityBottom != null){
                        priorityBottom.setBackgroundColor(itemView.getContext().getResources()
                                .getColor(R.color.btnpriority4));
                    }

                    break;
                case 5:
                    if (priorityTop!= null){
                        priorityTop.setBackgroundColor(itemView.getContext().getResources()
                                .getColor(R.color.btnpriority5));
                    }
                    if (priorityBottom != null){
                        priorityBottom.setBackgroundColor(itemView.getContext().getResources()
                                .getColor(R.color.btnpriority5));
                    }

                    break;
                case 6:
                    if (priorityTop!= null){
                        priorityTop.setBackgroundColor(itemView.getContext().getResources()
                                .getColor(R.color.btnpriority6));
                    }
                    if (priorityBottom != null){
                        priorityBottom.setBackgroundColor(itemView.getContext().getResources()
                                .getColor(R.color.btnpriority6));
                    }

                    break;
                case 7:
                    if (priorityTop!= null){
                        priorityTop.setBackgroundColor(itemView.getContext().getResources()
                                .getColor(R.color.btnpriority7));
                    }
                    if (priorityBottom != null){
                        priorityBottom.setBackgroundColor(itemView.getContext().getResources()
                                .getColor(R.color.btnpriority7));
                    }

                    break;
                case 8:
                    if (priorityTop!= null){
                        priorityTop.setBackgroundColor(itemView.getContext().getResources()
                                .getColor(R.color.btnpriority8));
                    }
                    if (priorityBottom != null){
                        priorityBottom.setBackgroundColor(itemView.getContext().getResources()
                                .getColor(R.color.btnpriority8));
                    }

                    break;
                case 9:
                    if (priorityTop!= null){
                        priorityTop.setBackgroundColor(itemView.getContext().getResources()
                                .getColor(R.color.btnpriority9));
                    }
                    if (priorityBottom != null){
                        priorityBottom.setBackgroundColor(itemView.getContext().getResources()
                                .getColor(R.color.btnpriority9));
                    }

                    break;
                case 10:
                    if (priorityTop!= null){
                        priorityTop.setBackgroundColor(itemView.getContext().getResources()
                                .getColor(R.color.btnpriority10));
                    }
                    if (priorityBottom != null){
                        priorityBottom.setBackgroundColor(itemView.getContext().getResources()
                                .getColor(R.color.btnpriority10));
                    }

            }
        }

        void bindData(final ListElement item){ // *RVCV
            // Se le ponen los valores de la lista a cada apartado
            taskName.setText(item.getTaskName());
            dueDate.setText(item.getDueDate());
            // Se le pone un if porque la categoría no está presente en la vista compacta
            if (category != null)
                category.setText(item.getCategory());
            status = item.getStatusLE();
            priority = item.getPriorityLE();

            // Se le ponen los colores al botón y el fondo al nombre de la tarea, en caso de que
            // esté seleccionada la opción.

            if (MainActivity.isPaintButtons()){
                setBorderColors(priority);
                // Si no está seleccionada la opción de pintar, se queda como default. Y el default
                // es el color establecido en el xml (color blanco).
            }

            if (MainActivity.isPaintTasks()){
                setStatusColor(status);
            }

            // Hace que se pinte la fecha si no esta terminado el pendiente. También, le pone el
            // cuadro vacío o con la palomita si está completado el pendiente.
            if (!item.getStatusLE().equals("Terminado")) {
                completed.setBackground(itemView.getResources().getDrawable(R.drawable.unchecked_box_foreground));
                if (OrdenarPendientes.numericDate(item.getDueDate()) < MainActivity.getCurrentNumericDate()) {
                    paintDay();
                } else {
                    unPaint();
                }
            } else {
                completed.setBackground(itemView.getResources().getDrawable(R.drawable.checked_box_foreground));
            }

        }

        // Pone los colores normales a la fecha.
        private void unPaint() {
            dueDate.setBackgroundColor(Color.TRANSPARENT); // Esto hace transparente el fondo.
            dueDate.setTextColor(itemView.getContext().getResources().
                    getColor(android.R.color.secondary_text_light));
        }

        // Pinta el texto donde está la fecha de color rojo claro, para indicar que va atrasado el
        // pendiente.
        private void paintDay() {
            dueDate.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.late));
            dueDate.setTextColor(itemView.getContext().getResources().getColor(R.color.late_t));
            dueDate.setPadding(10, 0, 10, 0);
        }

        // Método que se ejecuta cuando se presiona el viewHolder. El listener se puso en la última
        // línea del constructor ViewHolder.

        // <RVCL>
        @Override
        public void onClick(View v) {
            // Ejecuta el método onTaskClick, se le pasa como argumento la posición del pendiente
            // presionado. Se usa el método onTaskClick para abrir la pantalla donde se puede
            // modificar o borrar un pendiente.
            onTaskClickListener.onTaskClick(getAdapterPosition());
        }
        // </RVCL>
    }

    // <RVCL>
    // Esta interface permite detectar los clicks que da el usuario en los elementos.
    public interface OnTaskClickListener{
        void onTaskClick(int position);
        // </RVCL>
        void onButtonClick(int position);
    }


}
