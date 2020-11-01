package com.android.gan091.gramaudenar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OndaChoqueEnterramientoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OndaChoqueEnterramientoFragment#} factory method to
 * create an instance of this fragment.
 */
public class OndaChoqueEnterramientoFragment extends Fragment implements View.OnClickListener {

    private OnFragmentInteractionListener mListener;

    boolean start = true;
    float cumulative_facade_area,
          cumulative_window_area,
          gap_percentage =-1,
          gap_score,
          feature_score,
          pAb,
          pCar,
          result;
    int number_floor;
    long id_record = -1;
    AlertDialog d = null;

    ArrayAdapter<String> aa_windows_material,
                         aa_windows_frame_material,
                         aa_flooring_material,
                         aa_shock_wave_typology,
                         aa_burying_typology;
    BaseDeDatos bdP;
    Button btn_facade,btn_window,btn_door,btn_generate;
    Context context;
    EditText et_percentage_according_height, et_gap_percentage, et_percentage_according_gaps, et_result, et_observations;
    Spinner sp_windows_frame_material,
            sp_windows_material,
            sp_flooring_material,
            sp_shock_wave_typology, 
            sp_burying_typology;

    String [] options_windows_material = new String[] {" ","Vidrio","Madera"};
    String [] options_windows_frame_material = new String[] {" ","Madera","Aluminio","Hierro Forjado"};
    String [] options_flooring_material = new String[] {" ","Tierra","Madera","Concreto","Enchape"};

    String [] options_shock_wave_typology = new String[] {" ","Tipologia_1","Tipologia_2","Tipologia_3"};
    String [] options_burying_typology = new String[] {" ","Tipologia_1","Tipologia_2"};

    String previous_shock_wave_typolgy,
           id_house,
           windows_material,
           windows_frame_material,
           flooring_material,
           wall_material,
           shock_wave_typology,
           burying_typology,
           observations;
    TextView tv_gape_percentage, tv_percentage_according_gapes, tv_percentage_according_height;

    public OndaChoqueEnterramientoFragment() {

        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onClick(View view) {
        Intent i;
        Toast toast;
        Bundle b1 = new Bundle();
        b1.putString("idCasa", id_house);

        switch (view.getId()){
            case R.id.btnGenerar:
                guardarBD();
                break;
            case R.id.btnFachViv:
                i = new Intent(getActivity(), FachadaActivity.class);
                i.putExtras(b1);
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.left_in,R.anim.left_out);
                break;
            case R.id.btnVen:
                i = new Intent(getActivity(), VentanaActivity.class);
                i.putExtras(b1);
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.left_in,R.anim.left_out);
                break;
            case R.id.btnPuertas:
                i = new Intent(getActivity(), PuertaActivity.class);
                i.putExtras(b1);
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.left_in,R.anim.left_out);
                break;
            case R.id.tv_gap_percentage:
                toast=Toast.makeText(getActivity(),"Porcentaje segun Caracteristicas",Toast.LENGTH_SHORT);
                toast.show();
                break;
            case R.id.tv_perc_accor_gaps:
                toast=Toast.makeText(getActivity(),"Porcentaje segun Aberturas (Puertas y Ventanas)",Toast.LENGTH_SHORT);
                toast.show();
                break;
            case R.id.tv_perc_accor_height:
                toast=Toast.makeText(getActivity(),"Porcentaje segun Altura (N° de Pisos)",Toast.LENGTH_SHORT);
                toast.show();
                break;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();
        bdP = new BaseDeDatos(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_onda_choque_enterramiento, container, false);

        sp_windows_frame_material = v.findViewById(R.id.spVentana);
        sp_windows_material = v.findViewById(R.id.spMatVentana);
        sp_shock_wave_typology = v.findViewById(R.id.sp_shock_wave_typology);
        sp_burying_typology = v.findViewById(R.id.sp_burying_typology);
        sp_flooring_material = v.findViewById(R.id.spMatPiso);

        btn_facade = v.findViewById(R.id.btnFachViv);
        btn_window = v.findViewById(R.id.btnVen);
        btn_door = v.findViewById(R.id.btnPuertas);
        btn_generate = v.findViewById(R.id.btnGenerar);

        tv_gape_percentage = v.findViewById(R.id.tv_gap_percentage);
        tv_percentage_according_gapes = v.findViewById(R.id.tv_perc_accor_gaps);
        tv_percentage_according_height = v.findViewById(R.id.tv_perc_accor_height);

        aa_windows_frame_material = new ArrayAdapter<>(context,android.R.layout.simple_spinner_item, options_windows_frame_material);
        aa_windows_material = new ArrayAdapter<>(context,android.R.layout.simple_spinner_item, options_windows_material);
        aa_shock_wave_typology = new ArrayAdapter<>(context,android.R.layout.simple_spinner_item, options_shock_wave_typology);
        aa_burying_typology = new ArrayAdapter<>(context,android.R.layout.simple_spinner_item, options_burying_typology);
        aa_flooring_material = new ArrayAdapter<>(context,android.R.layout.simple_spinner_item, options_flooring_material);

        sp_windows_frame_material.setAdapter(aa_windows_frame_material);
        sp_windows_material.setAdapter(aa_windows_material);
        sp_shock_wave_typology.setAdapter(aa_shock_wave_typology);
        sp_burying_typology.setAdapter(aa_burying_typology);
        sp_flooring_material.setAdapter(aa_flooring_material);

        btn_facade.setOnClickListener(this);
        btn_window.setOnClickListener(this);
        btn_door.setOnClickListener(this);
        btn_generate.setOnClickListener(this);

        tv_gape_percentage.setOnClickListener(this);
        tv_percentage_according_gapes.setOnClickListener(this);
        tv_percentage_according_height.setOnClickListener(this);

        et_gap_percentage = v.findViewById(R.id.et_gap_percentage);

        et_percentage_according_height = v.findViewById(R.id.et_perc_accor_height);
        et_percentage_according_gaps = v.findViewById(R.id.et_perc_accor_gaps);
        et_result = v.findViewById(R.id.et_result);
        et_observations = v.findViewById(R.id.etObservaciones);

        Bundle bundle = getActivity().getIntent().getExtras();

        id_house = bundle.getString("idcasa");

        cargarDatos();

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Esta interfaz debe implementarse mediante actividades que cotengan este
     * fragmento para permitir que una interaccion en este fragmento
     * se comunique a la actividad y potencialmente a otros fragmentos contenidos
     * en esta actividad.
     * Ver la leccion de entrenamiento de Android <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Comunicación con otros fragmentos</a> para mas información.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void cargarDatos(){
        bdP.abrirBD();
        Cursor cursor = bdP.cargarDatos_ID_RID(id_house,"tblOndaChoque");
        /*
        0 -> RowId
        1 -> IdCasa
        2 -> Material Ventana
        3 -> Material Marco de Ventana
        4 -> Material de Piso
        5 -> Material de Muros
        6 -> Tipologia Onda de Choque
        7 -> Tipologia Enterramiento
        8 -> Observaciones
         */

        if (cursor.moveToFirst()){
            /*
            Tipos
            1 -> Material de Ventana
            2 -> Material de Marco de Ventana
            3 -> Material de Pisos
             */
            do {
                id_record = cursor.getLong(0);
                setSpinner(cursor.getString(2),1);
                setSpinner(cursor.getString(3),2);
                setSpinner(cursor.getString(4),3);
                setSpinner(cursor.getString(6),4);
                setSpinner(cursor.getString(7),5);
                et_observations.setText(cursor.getString(8));
            }while (cursor.moveToNext());
        }
        bdP.cerrarBD();
    }

    public void guardarBD(){
        //inicio = true;

        /*Se asigna a variables locales las selecciones de las listas de materiales tanto para
          ventanas, marco de ventanas, pisos y las observaciones que se hacen con respecto a la
          vivienda*/
        windows_frame_material = sp_windows_frame_material.getSelectedItem().toString();
        windows_material = sp_windows_material.getSelectedItem().toString();
        flooring_material = sp_flooring_material.getSelectedItem().toString();

        observations = et_observations.getText().toString();

        if (bdP.existeRegistro_RID(id_record,"tblondachoque")){

            AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());

            builder1.setTitle("Registro Existente")
                    .setMessage("Ya hay registros almacenados previamente ¿Desea actualizar los registros?")
                    .setPositiveButton("Si", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            tipificacionOndaChoque();
                            tipificacionEnterramiento();

                            try {
                                bdP.abrirBD();
                                bdP.updateOndadeChoque_RID(id_record, windows_material, windows_frame_material, flooring_material, shock_wave_typology, burying_typology, observations.replaceAll("\n", ""));
                                bdP.cerrarBD();

                                Toast toast;
                                toast=Toast.makeText(context,"Actualizacion de Registro Completa",Toast.LENGTH_SHORT);
                                toast.show();
                            }
                            catch (Exception e){
                                Toast toast;
                                toast=Toast.makeText(context,e.toString(),Toast.LENGTH_LONG);
                                toast.show();
                            }
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            d.dismiss();
                        }
                    });
            d = builder1.create();
            d.show();
        }else {
            if (!bdP.existeRegistro_ID("tblfachada",id_house) &&
                !bdP.existeRegistro_ID("tblventana",id_house) &&
                !bdP.existeRegistro_ID("tblpuerta",id_house) &&
                TextUtils.isEmpty(et_observations.getText().toString())
            ){
                et_observations.requestFocus();
                et_observations.setError("Al menos ingresar las observaciones de porque no se ha registrado ningun dato");
            }else {
                tipificacionOndaChoque();
                tipificacionEnterramiento();
                try {
                    id_record = bdP.insertarOndaChoqueEnterramiento_ID(id_house,
                            windows_material,
                            windows_frame_material,
                            flooring_material,
                            wall_material,
                            shock_wave_typology,
                            burying_typology,
                            observations.replaceAll("\n", ""));
                    start = false;
                    Toast toast;
                    toast=Toast.makeText(context,"Tipologia Onda de Choque - Guardado Exitoso",Toast.LENGTH_SHORT);
                    toast.show();
                }
                catch (Exception e){
                    Toast toast;
                    toast=Toast.makeText(context,e.toString(),Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        }
    }

    public void setSpinner(String opc, int tipo){

        //Este metodo se encarga se asignar en la interfaz los valores que se descargan de la base de datos

        switch (tipo){
            case 1:
                switch (opc){
                    case " ":
                        sp_windows_material.setSelection(0);
                        break;
                    case "Vidrio":
                        sp_windows_material.setSelection(1);
                        break;
                    case "Madera":
                        sp_windows_material.setSelection(2);
                        break;
                }
                break;
            case 2:
                switch (opc){
                    case " ":
                        sp_windows_frame_material.setSelection(0);
                        break;
                    case "Madera":
                        sp_windows_frame_material.setSelection(1);
                        break;
                    case "Aluminio":
                        sp_windows_frame_material.setSelection(2);
                        break;
                    case "Hierro Forjado":
                        sp_windows_frame_material.setSelection(3);
                        break;
                }
                break;
            case 3:
                switch (opc){
                    case " ":
                        sp_flooring_material.setSelection(0);
                        break;
                    case "Tierra":
                        sp_flooring_material.setSelection(1);
                        break;
                    case "Madera":
                        sp_flooring_material.setSelection(2);
                        break;
                    case "Concreto":
                        sp_flooring_material.setSelection(3);
                        break;
                    case "Enchape":
                        sp_flooring_material.setSelection(4);
                        break;
                }
                break;
            case 4:
                switch (opc){
                    case "Tipologia_1":
                        sp_shock_wave_typology.setSelection(1);
                        break;
                    case "Tipologia_2":
                        sp_shock_wave_typology.setSelection(2);
                        break;
                    case "Tipologia_3":
                        sp_shock_wave_typology.setSelection(3);
                        break;
                }
                break;
            case 5:
                switch (opc){
                    case "Tipologia_1":
                        sp_burying_typology.setSelection(1);
                        break;
                    case "Tipologia_2":
                        sp_burying_typology.setSelection(2);
                        break;
                }
                break;
        }
    }

    public void tipificacionEnterramiento() {

        float alturaFachada = 0,
              areaAcumuladaFachada = 0, // Verificar si se puede utilizar con las variables locales
              areaAcumuladaPuertas = 0,
              areaAcumuladaVentanas = 0;

        bdP.abrirBD();
        Cursor cursor_fachada_te = bdP.cargarDatos_ID_RID(id_house, "tblFachada");

        Cursor cursor_puertas_te = bdP.cargarDatos_ID_RID(id_house, "tblPuerta");

        Cursor cursor_ventanas_te = bdP.cargarDatosVentanaP1_RID(id_house);

        /*
                    cursor_fachada_te        cursor_puertas_te               cursor_ventanas_te
            0 -->       RowId                       RowId                           RowId
            1 -->       IdCasa                      IdCasa                          IdCasa
            2 -->       Ancho                       Ancho                           Ancho
            3 -->       Altura                      Altura                          Altura
            4 -->       Area                        Area                            Area
            5 -->       AreaPiso1                     -                             NumeroPiso
         */

        if (cursor_fachada_te.moveToFirst()) {

            //Recorrer el cursor_fachada_te, obtener el area acumulada solo del primer piso
            // y obtener la altura de la edificacion
            do {
                areaAcumuladaFachada = areaAcumuladaFachada + cursor_fachada_te.getFloat(5);
                alturaFachada = cursor_fachada_te.getFloat(3);
            } while (cursor_fachada_te.moveToNext());
        }
        else{
            areaAcumuladaFachada = 1;
        }

        if (cursor_ventanas_te.moveToFirst()){
            //Recorrer el cursorVentanas y obtener el area acumulada solo del primer piso
            do {
                areaAcumuladaVentanas = areaAcumuladaVentanas + cursor_ventanas_te.getFloat(4);
            } while (cursor_ventanas_te.moveToNext());
        }

        if (cursor_puertas_te.moveToFirst()){
            //Recorrer el cursorPuertas y obtener el area acumulada, y como solo se tienen en
            // cuenta las puertas del primer piso se toma toda las areas registradas
            do {
                areaAcumuladaPuertas = areaAcumuladaPuertas + cursor_puertas_te.getFloat(4);
            } while (cursor_puertas_te.moveToNext());
        }

        //Se obtiene el numero de pisos de la edificacion al dividir la altura de la Fachada
        // entre la medida estandar asignada de un piso de una vivienda
        number_floor = (int) (alturaFachada / 2.8) + 1;

        //Se obtiene el porcentaje de aberturas al dividir las areas acumuladas combinadas de
        // puertas y ventanas con el area acumulada de la fachada y se multiplica por 100
        gap_percentage = ((areaAcumuladaVentanas + areaAcumuladaPuertas) / areaAcumuladaFachada) * 100;

        // Se le asigna una puntuacion de aberturas de acuerdo al porcentaje obtenido de las mismas
        // Si porcentaje de aberturas es menor a 10% se le asigna un valor de 1
        // Si porcentaje de aberturas esta entre 10% y 50% se le asigna un valor de 5
        // Si porcentaje de aberturas es mayor a 50% se le asigna un valor de 10
        if (gap_percentage < 10) {
            gap_score = (float) 1;
        } else if (gap_percentage > 50) {
            gap_score = (float) 10;
        } else {
            if (gap_percentage >= 10 && gap_percentage <= 50) {
                gap_score = (float) 5;
            }
        }


        // Se le asigna una puntuacion de caracteristicas de acuerdo al numero de pisos de la vivienda
        // Si numero de pisos es igual a 1 se le asigna un valor de 10
        // Si numero de pisos es mayor de 1 se le asigna un valor de 5
        if(number_floor == 1){
            feature_score = (float) 10;
        }
        else{
            if (number_floor > 1){
                feature_score = (float) 5;
            }
            else {
                feature_score = (float) 0;
            }
        }

        // A cada puntuacion le corresponde un porcentaje de influencia en el resultado final
        // es asi que para el porcentaje de aberturas tiene un 70% y
        // para las caracteristicas fisicas se tiene un 30%
        pAb = (float)(gap_score * 0.7);
        pCar = (float)(feature_score * 0.3);

        // Al sumar los resultados obtenidos tanto de aberturas como de caracteristicas y despues de aplicar sus
        // respectivos porcentajes, se obtiene el resultado que definira el tipo de Tipologia Estructural frente
        // a la amenaza volcanica enterramiento que aplica para la vivienda
        result = pAb + pCar;

        // Se muestra en la interfaz los resultados obtenidos al realizar los calculos para determinar la tipologia
        // estructural frente a la amenaza volcanica de enterramiento
        et_percentage_according_height.setText(Float.toString(bdP.reducirFloat(pCar)));
        et_percentage_according_gaps.setText(Float.toString(bdP.reducirFloat(pAb)));
        et_result.setText(Float.toString(bdP.reducirFloat(result)));

        // Tipologias
        // 0 -> Sin Tipologia
        // 1 -> Tipologia 1 => Si el resultado esta entre 5 y 10
        // 2 -> Tipologia 2 => Si el resultado es menor de 5 o
        //                     si el resultado esta entre 5 y 10 pero el porcentaje de aberturas es 0

        if(result < 5){
            sp_burying_typology.setSelection(2);
        }
        else {
            if (result <= 10) {
                if (gap_score == 0) {
                    sp_burying_typology.setSelection(2);
                } else {
                    sp_burying_typology.setSelection(1);
                }
            }
        }

        // Se muestra en la interfaz el resultado de la Tipologia Estructural generada en base a la amenaza
        // volcanica de Enterramiento
        burying_typology = sp_burying_typology.getSelectedItem().toString();
        bdP.cerrarBD();
    }

    public void tipificacionOndaChoque(){
        cumulative_facade_area = 0;
        cumulative_window_area = 0;

        bdP.abrirBD();

        Cursor cursorFachada = bdP.cargarDatos_ID_RID(id_house,"tblFachada");
        Cursor cursorVentanas = bdP.cargarDatos_ID_RID(id_house,"tblVentana");

        /*
                    cursorFachada      cursorVentanas
            0 -->       RowId              RowId
            1 -->       IdCasa             IdCasa
            2 -->       Ancho              Ancho
            3 -->       Altura             Altura
            4 -->       Area               Area
            5 -->       AreaPiso1        NumeroPiso
         */

        if(cursorFachada.moveToFirst() && cursorVentanas.moveToFirst()){

            //Recorrer el cursorFachada y obtener el area acumulada
            do {
                cumulative_facade_area = cumulative_facade_area + cursorFachada.getFloat(4);
            }while (cursorFachada.moveToNext());
            Log.i("Area Acumulada Fachada",Float.toString(cumulative_facade_area));

            //Recorrer el cursorVentanas y obtener el area acumulada
            do {
                cumulative_window_area = cumulative_window_area + cursorVentanas.getFloat(4);
            }while (cursorVentanas.moveToNext());
            Log.i("Area Acumulada Ventanas",Float.toString(cumulative_window_area));
        }

        // Se obtiene el porcentaje de aberturas al dividir el area acumulada de
        //  ventanas con el area acumulada de la fachada y se multiplica por 100
        gap_percentage = (cumulative_window_area / cumulative_facade_area)*100;
        Log.i("Porcentaje de Aberturas",Float.toString(gap_percentage));

        // Se realiza esta validacion que ocurre cuando se registran mal los datos ya sea de ventanas o fachada
        if (gap_percentage >100){
            gap_percentage =100;

            AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());

            builder1.setTitle("Inconsistencia en los Registros")
                    .setMessage("Verificar los datos registrados en los formularios de Fachada y/o Ventanas, ya que presentan inconsistencias")
                    .setPositiveButton("Aceptar",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    d.dismiss();
                                }
                            })
                    .setIcon(R.drawable.common_google_signin_btn_icon_disabled);
            d = builder1.create();
            d.show();
        }

        // Se muestra en la interfaz el resultado del Porcentaje de Aberturas obtenido al realizar los calculos previos
        // para la generar la Tipologia Estructural generada en base a la amenaza volcanica de Onda de Choque
        et_gap_percentage.setText(Float.toString(bdP.reducirFloat(gap_percentage)));

        // Tipologias
        // 0 -> Sin Tipologia
        // 1 -> Tipologia 1 => Si el porcentaje de aberturas es mayor de 50
        // 2 -> Tipologia 2 => Si el porcentaje de aberturas esta entre 10 y 50
        // 3 -> Tipologia 3 => Si el porcentaje de aberturas esta entre 0 y 10
        if(gap_percentage < 10 && gap_percentage >= 0){
            sp_shock_wave_typology.setSelection(3);
        }
        else{
            if (gap_percentage > 50) {
                sp_shock_wave_typology.setSelection(1);
            }
            else{
                if (gap_percentage >= 10 && gap_percentage <= 50) {
                    sp_shock_wave_typology.setSelection(2);
                }
                else {
                    sp_shock_wave_typology.setSelection(0);
                }
            }
        }

        // Se muestra en la interfaz el resultado de la Tipologia Estructural generada en base a la amenaza
        // volcanica de Onda de Choque
        shock_wave_typology = sp_shock_wave_typology.getSelectedItem().toString();
        previous_shock_wave_typolgy = shock_wave_typology; //Intentar cambiar la tipologia generada de acuerdo a la evaluacion del revisor
        bdP.cerrarBD();
    }
}