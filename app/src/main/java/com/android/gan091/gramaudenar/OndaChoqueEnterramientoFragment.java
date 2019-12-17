package com.android.gan091.gramaudenar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

    String tipOCHAnterior;
    boolean inicio = true;
    float areaAcumuladaFachada, areaAcumuladaVentanas,porcentajeAberturas=-1,puntajeAb,puntajeCar,pAb,pCar,resultado;
    int nPisos,idCasa;
    long idRegistro = -1;
    AlertDialog d = null;

    ArrayAdapter<String> aaMVentana,aaMatVentana,aaMatPisos,aaTO,aaTE;
    BaseDeDatos bdP;
    Button btnFac,btnVentana,btnPuerta,btnGenerar;
    Context context;
    EditText etPCE, etPAOCH, etPAE, etResultadoE,etObservaciones;
    Spinner spMarVen,spMatVen,spMatPisos,spTipOCH,spTipE;

    String [] opcMatVentana = new String[] {" ","Vidrio","Madera"};
    String [] opcMarVentana = new String[] {" ","Madera","Aluminio","Hierro Forjado"};
    String [] opcMatPisos = new String[] {" ","Tierra","Madera","Concreto","Enchape"};

    String [] opcTipOCh = new String[] {" ","Tipologia_1","Tipologia_2","Tipologia_3"};
    String [] opcTipE = new String[] {" ","Tipologia_1","Tipologia_2"};

    String materialVen,marcoVentana,materialPisos,materialMuros,tipOndaChoque,tipEnterramiento,observaciones;
    TextView tvA,tvA1,tvAlt;

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
        b1.putInt("idCasa",idCasa);

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
            case R.id.tvPAb:
                toast=Toast.makeText(getActivity(),"Porcentaje segun Caracteristicas",Toast.LENGTH_SHORT);
                toast.show();
                break;
            case R.id.tvAb1:
                toast=Toast.makeText(getActivity(),"Porcentaje segun Aberturas (Puertas y Ventanas)",Toast.LENGTH_SHORT);
                toast.show();
                break;
            case R.id.tvAlt:
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

        spMarVen = v.findViewById(R.id.spVentana);
        spMatVen = v.findViewById(R.id.spMatVentana);
        spTipOCH = v.findViewById(R.id.spTipOn);
        spTipE = v.findViewById(R.id.spTipEnt);
        spMatPisos = v.findViewById(R.id.spMatPiso);

        btnFac = v.findViewById(R.id.btnFachViv);
        btnVentana = v.findViewById(R.id.btnVen);
        btnPuerta = v.findViewById(R.id.btnPuertas);
        btnGenerar = v.findViewById(R.id.btnGenerar);

        tvA = v.findViewById(R.id.tvPAb);
        tvA1 = v.findViewById(R.id.tvAb1);
        tvAlt = v.findViewById(R.id.tvAlt);

        aaMVentana = new ArrayAdapter<>(context,android.R.layout.simple_spinner_item,opcMarVentana);
        aaMatVentana = new ArrayAdapter<>(context,android.R.layout.simple_spinner_item,opcMatVentana);
        aaTO = new ArrayAdapter<>(context,android.R.layout.simple_spinner_item,opcTipOCh);
        aaTE = new ArrayAdapter<>(context,android.R.layout.simple_spinner_item,opcTipE);
        aaMatPisos = new ArrayAdapter<>(context,android.R.layout.simple_spinner_item,opcMatPisos);

        spMarVen.setAdapter(aaMVentana);
        spMatVen.setAdapter(aaMatVentana);
        spTipOCH.setAdapter(aaTO);
        spTipE.setAdapter(aaTE);
        spMatPisos.setAdapter(aaMatPisos);

        btnFac.setOnClickListener(this);
        btnVentana.setOnClickListener(this);
        btnPuerta.setOnClickListener(this);
        btnGenerar.setOnClickListener(this);

        tvA.setOnClickListener(this);
        tvA1.setOnClickListener(this);
        tvAlt.setOnClickListener(this);

        etPAOCH = v.findViewById(R.id.etPAb);

        etPCE = v.findViewById(R.id.etPCE);
        etPAE = v.findViewById(R.id.etPAE);
        etResultadoE = v.findViewById(R.id.etRE);
        etObservaciones = v.findViewById(R.id.etObservaciones);

        Bundle bundle = getActivity().getIntent().getExtras();

        idCasa = bundle.getInt("idcasa");

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
        Cursor cursor = bdP.cargarDatosTablas(idCasa,"tblOndaChoque");
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
                setSpinner(cursor.getString(2),1);
                setSpinner(cursor.getString(3),2);
                setSpinner(cursor.getString(4),3);
                etObservaciones.setText(cursor.getString(8));
                idRegistro = cursor.getLong(0);
            }while (cursor.moveToNext());
            Log.i("Cas->IdCasaOCH",Integer.toString(idCasa));

            tipificacionOndaChoque();//mirar si es mas practico cargar dato desde cursor
            tipificacionEnterramiento();
        }
        bdP.cerrarBD();
    }

    public void guardarBD(){
        //inicio = true;

        /*Se asigna a variables locales las selecciones de las listas de materiales tanto para
          ventanas, marco de ventanas, pisos y las observaciones que se hacen con respecto a la
          vivienda*/
        marcoVentana = spMarVen.getSelectedItem().toString();
        materialVen = spMatVen.getSelectedItem().toString();
        materialPisos = spMatPisos.getSelectedItem().toString();

        observaciones = etObservaciones.getText().toString();

        if (bdP.existeRegistro(idRegistro,"tblondachoque")){

            AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());

            builder1.setTitle("Registro Existente")
                    .setMessage("Ya hay registros almacenados previamente ¿Desea actualizar los registros?")
                    .setPositiveButton("Si",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    tipificacionOndaChoque();
                                    tipificacionEnterramiento();

                                    try {
                                        bdP.abrirBD();
                                        bdP.updateOndadeChoque(idRegistro,materialVen,marcoVentana,materialPisos,tipOndaChoque,tipEnterramiento,observaciones.replaceAll("\n", ""));
                                        bdP.cerrarBD();

                                        Toast toast;
                                        toast=Toast.makeText(context,"Actualizacion de Registro Completada",Toast.LENGTH_SHORT);
                                        toast.show();
                                    }
                                    catch (Exception e){
                                        Toast toast;
                                        toast=Toast.makeText(context,e.toString(),Toast.LENGTH_LONG);
                                        toast.show();
                                    }
                                }
                            })
                    .setNegativeButton("No",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    d.dismiss();
                                }
                            });
            d = builder1.create();
            d.show();
        }else {
            tipificacionOndaChoque();
            tipificacionEnterramiento();
            try {
                idRegistro = bdP.insertarOndaChoqueEnterramiento(
                        idCasa,
                        materialVen,
                        marcoVentana,
                        materialPisos,
                        materialMuros,
                        tipOndaChoque,
                        tipEnterramiento,
                        observaciones.replaceAll("\n", ""));

                //Log.i("Tipologia",tipEnterramiento);
                inicio = false;
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

    public void setSpinner(String opc, int tipo){

        //Este metodo se encarga se asignar en la interfaz los valores que se descargan de la base de datos

        switch (tipo){
            case 1:
                switch (opc){
                    case "Vidrio":
                        spMatVen.setSelection(1);
                        break;
                    case "Madera":
                        spMatVen.setSelection(2);
                        break;
                }
                break;
            case 2:
                switch (opc){
                    case "Madera":
                        spMarVen.setSelection(1);
                        break;
                    case "Aluminio":
                        spMarVen.setSelection(2);
                        break;
                    case "Hierro Forjado":
                        spMarVen.setSelection(3);
                        break;
                }
                break;
            case 3:
                switch (opc){
                    case "Tierra":
                        spMatPisos.setSelection(1);
                        break;
                    case "Madera":
                        spMatPisos.setSelection(2);
                        break;
                    case "Concreto":
                        spMatPisos.setSelection(3);
                        break;
                    case "Enchape":
                        spMatPisos.setSelection(4);
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
        Cursor cursorFachada = bdP.cargarDatosTablas(idCasa, "tblFachada");

        Cursor cursorPuertas = bdP.cargarDatosTablas(idCasa, "tblPuerta");

        Cursor cursorVentanas = bdP.cargarDatosTablas1(idCasa);

        /*
                    cursorFachada               cursorPuertas                   cursorVentanas
            0 -->       RowId                       RowId                           RowId
            1 -->       IdCasa                      IdCasa                          IdCasa
            2 -->       Ancho                       Ancho                           Ancho
            3 -->       Altura                      Altura                          Altura
            4 -->       Area                        Area                            Area
            5 -->       AreaPiso1                     -                             NumeroPiso
         */

        if (cursorFachada.moveToFirst()) {

            //Recorrer el cursorFachada, obtener el area acumulada solo del primer piso
            // y obtener la altura de la edificacion
            do {
                areaAcumuladaFachada = areaAcumuladaFachada + cursorFachada.getFloat(5);
                alturaFachada = cursorFachada.getFloat(3);
            } while (cursorFachada.moveToNext());
        }
        else{
            areaAcumuladaFachada = 1;
        }

        if (cursorVentanas.moveToFirst()){
            //Recorrer el cursorVentanas y obtener el area acumulada solo del primer piso
            do {
                areaAcumuladaVentanas = areaAcumuladaVentanas + cursorVentanas.getFloat(4);
            } while (cursorVentanas.moveToNext());
        }

        if (cursorPuertas.moveToFirst()){
            //Recorrer el cursorPuertas y obtener el area acumulada, y como solo se tienen en
            // cuenta las puertas del primer piso se toma toda las areas registradas
            do {
                areaAcumuladaPuertas = areaAcumuladaPuertas + cursorPuertas.getFloat(4);
            } while (cursorPuertas.moveToNext());
        }

        //Se obtiene el numero de pisos de la edificacion al dividir la altura de la Fachada
        // entre la medida estandar asignada de un piso de una vivienda
        nPisos = (int) (alturaFachada / 2.8) + 1;

        //Se obtiene el porcentaje de aberturas al dividir las areas acumuladas combinadas de
        // puertas y ventanas con el area acumulada de la fachada y se multiplica por 100
        porcentajeAberturas = ((areaAcumuladaVentanas + areaAcumuladaPuertas) / areaAcumuladaFachada) * 100;

        // Se le asigna una puntuacion de aberturas de acuerdo al porcentaje obtenido de las mismas
        // Si porcentaje de aberturas es menor a 10% se le asigna un valor de 1
        // Si porcentaje de aberturas esta entre 10% y 50% se le asigna un valor de 5
        // Si porcentaje de aberturas es mayor a 50% se le asigna un valor de 10
        if (porcentajeAberturas < 10) {
            puntajeAb = (float) 1;
        } else if (porcentajeAberturas > 50) {
            puntajeAb = (float) 10;
        } else {
            if (porcentajeAberturas >= 10 && porcentajeAberturas <= 50) {
                puntajeAb = (float) 5;
            }
        }


        // Se le asigna una puntuacion de caracteristicas de acuerdo al numero de pisos de la vivienda
        // Si numero de pisos es igual a 1 se le asigna un valor de 10
        // Si numero de pisos es mayor de 1 se le asigna un valor de 5
        if(nPisos == 1){
            puntajeCar = (float) 10;
        }
        else{
            if (nPisos > 1){
                puntajeCar = (float) 5;
            }
            else {
                puntajeCar = (float) 0;
            }
        }

        // A cada puntuacion le corresponde un porcentaje de influencia en el resultado final
        // es asi que para el porcentaje de aberturas tiene un 70% y
        // para las caracteristicas fisicas se tiene un 30%
        pAb = (float)(puntajeAb * 0.7);
        pCar = (float)(puntajeCar * 0.3);

        // Al sumar los resultados obtenidos tanto de aberturas como de caracteristicas y despues de aplicar sus
        // respectivos porcentajes, se obtiene el resultado que definira el tipo de Tipologia Estructural frente
        // a la amenaza volcanica enterramiento que aplica para la vivienda
        resultado = pAb + pCar;

        // Se muestra en la interfaz los resultados obtenidos al realizar los calculos para determinar la tipologia
        // estructural frente a la amenaza volcanica de enterramiento
        etPCE.setText(Float.toString(bdP.reducirFloat(pCar)));
        etPAE.setText(Float.toString(bdP.reducirFloat(pAb)));
        etResultadoE.setText(Float.toString(bdP.reducirFloat(resultado)));

        // Tipologias
        // 0 -> Sin Tipologia
        // 1 -> Tipologia 1 => Si el resultado esta entre 5 y 10
        // 2 -> Tipologia 2 => Si el resultado es menor de 5 o
        //                     si el resultado esta entre 5 y 10 pero el porcentaje de aberturas es 0

        if(resultado < 5){
            spTipE.setSelection(2);
        }
        else {
            if (resultado <= 10) {
                if (puntajeAb == 0) {
                    spTipE.setSelection(2);
                } else {
                    spTipE.setSelection(1);
                }
            }
        }

        // Se muestra en la interfaz el resultado de la Tipologia Estructural generada en base a la amenaza
        // volcanica de Enterramiento
        tipEnterramiento = spTipE.getSelectedItem().toString();
        bdP.cerrarBD();
    }

    public void tipificacionOndaChoque(){
        areaAcumuladaFachada = 0;
        areaAcumuladaVentanas = 0;

        bdP.abrirBD();

        Cursor cursorFachada = bdP.cargarDatosTablas(idCasa,"tblFachada");
        Cursor cursorVentanas = bdP.cargarDatosTablas(idCasa,"tblVentana");

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
                areaAcumuladaFachada = areaAcumuladaFachada + cursorFachada.getFloat(4);
            }while (cursorFachada.moveToNext());
            Log.i("Area Acumulada Fachada",Float.toString(areaAcumuladaFachada));

            //Recorrer el cursorVentanas y obtener el area acumulada
            do {
                areaAcumuladaVentanas = areaAcumuladaVentanas + cursorVentanas.getFloat(4);
            }while (cursorVentanas.moveToNext());
            Log.i("Area Acumulada Ventanas",Float.toString(areaAcumuladaVentanas));
        }

        // Se obtiene el porcentaje de aberturas al dividir el area acumulada de
        //  ventanas con el area acumulada de la fachada y se multiplica por 100
        porcentajeAberturas = (areaAcumuladaVentanas / areaAcumuladaFachada)*100;
        Log.i("Porcentaje de Aberturas",Float.toString(porcentajeAberturas));

        // Se realiza esta validacion que ocurre cuando se registran mal los datos ya sea de ventanas o fachada
        if (porcentajeAberturas>100){
            porcentajeAberturas=100;

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
        etPAOCH.setText(Float.toString(bdP.reducirFloat(porcentajeAberturas)));

        // Tipologias
        // 0 -> Sin Tipologia
        // 1 -> Tipologia 1 => Si el porcentaje de aberturas es mayor de 50
        // 2 -> Tipologia 2 => Si el porcentaje de aberturas esta entre 10 y 50
        // 3 -> Tipologia 3 => Si el porcentaje de aberturas esta entre 0 y 10
        if(porcentajeAberturas < 10 && porcentajeAberturas >= 0){
            spTipOCH.setSelection(3);
        }
        else{
            if (porcentajeAberturas > 50) {
                spTipOCH.setSelection(1);
            }
            else{
                if (porcentajeAberturas >= 10 && porcentajeAberturas <= 50) {
                    spTipOCH.setSelection(2);
                }
                else {
                    spTipOCH.setSelection(0);
                }
            }
        }

        // Se muestra en la interfaz el resultado de la Tipologia Estructural generada en base a la amenaza
        // volcanica de Onda de Choque
        tipOndaChoque = spTipOCH.getSelectedItem().toString();
        tipOCHAnterior = tipOndaChoque; //Intentar cambiar la tipologia generada de acuerdo a la evaluacion del revisor
        bdP.cerrarBD();
    }
}