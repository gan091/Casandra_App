package com.android.gan091.gramaudenar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LaharesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LaharesFragment#} factory method to
 * create an instance of this fragment.
 */
public class LaharesFragment extends Fragment implements View.OnClickListener{

    private OnFragmentInteractionListener mListener;
    String id_house;
    float puntajeEst,puntajeMuros,puntajeEstEd,resTipologia;
    long idRegistro = -1;
    AlertDialog d = null;

    ArrayAdapter<String> aaMuros,aaEGE,aaTipLahares;
    BaseDeDatos bdP;
    Button btnLahares;
    CheckBox cbReforzado;
    Context context;
    EditText etEst,etMuros,etEstEd,etResTip,etObservaciones;
    Spinner spMuros,spEstadoGeneral,spTipLahares;
    String [] opcMuros = new String[] {" ","Ladrillo Macizo","Bloque","Tapia Pisada","Bahareque","Madera"};
    String [] opcEstadoGeneral = new String[] {" ","Bueno","Regular","Malo"};
    String [] opcTipLahares = new String[] {" ","Tipologia_1","Tipologia_2","Tipologia_3"};
    TextView tvEstructura,tvMuros,tvEstadoGeneral;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();
        bdP = new BaseDeDatos(context);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onClick(View view) {
        Toast toast;

        switch (view.getId()){
            case R.id.tvEst:
                toast= Toast.makeText(getActivity(),"Porcentaje segun Estructura",Toast.LENGTH_SHORT);
                toast.show();
                break;
            case R.id.tvMur:
                toast=Toast.makeText(getActivity(),"Porcentaje segun Material de Muros",Toast.LENGTH_SHORT);
                toast.show();
                break;
            case R.id.tvEstado:
                toast=Toast.makeText(getActivity(),"Porcentaje segun Estado General de la Vivienda",Toast.LENGTH_SHORT);
                toast.show();
                break;
            case R.id.btnGenerarL:
                guardarBD();
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_lahares, container, false);

        spMuros = v.findViewById(R.id.spMuros);
        spEstadoGeneral = v.findViewById(R.id.spEstGenEd);
        spTipLahares = v.findViewById(R.id.spLahares);

        tvEstructura = v.findViewById(R.id.tvEst);
        tvMuros = v.findViewById(R.id.tvMur);
        tvEstadoGeneral = v.findViewById(R.id.tvEstado);

        btnLahares = v.findViewById(R.id.btnGenerarL);

        cbReforzado = v.findViewById(R.id.checkBox);

        etEst = v.findViewById(R.id.etPE);
        etMuros = v.findViewById(R.id.etPMM);
        etEstEd = v.findViewById(R.id.etPEG);
        etResTip = v.findViewById(R.id.etResultadoLahares);
        etObservaciones = v.findViewById(R.id.etObservacionesLahares);

        aaMuros = new ArrayAdapter<>(this.getContext(),android.R.layout.simple_spinner_item,opcMuros);
        aaEGE = new ArrayAdapter<>(this.getContext(),android.R.layout.simple_spinner_item,opcEstadoGeneral);
        aaTipLahares = new ArrayAdapter<>(this.getContext(),android.R.layout.simple_spinner_item,opcTipLahares);

        spMuros.setAdapter(aaMuros);
        spEstadoGeneral.setAdapter(aaEGE);
        spTipLahares.setAdapter(aaTipLahares);

        tvEstructura.setOnClickListener(this);
        tvMuros.setOnClickListener(this);
        tvEstadoGeneral.setOnClickListener(this);

        btnLahares.setOnClickListener(this);

        cbReforzado.isChecked();

        Bundle bundle = getActivity().getIntent().getExtras();

        id_house = bundle.getString("idcasa");

        cargarDatos();

        return v;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void guardarBD(){

        if (bdP.existeRegistro_RID(idRegistro,"tbllahares")){
            AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());

            builder1.setTitle("Registro Existente")
                    .setMessage("Ya hay registros almacenados previamente ¿Desea actualizar los registros?")
                    .setPositiveButton("Si", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            obtenerTipologia();

                            try {
                                bdP.abrirBD();
                                bdP.updateLahares_RID(idRegistro,
                                                      cbReforzado.isChecked(),
                                                      spMuros.getSelectedItem().toString(),
                                                      spEstadoGeneral.getSelectedItem().toString(),
                                                      spTipLahares.getSelectedItem().toString(),
                                                      etObservaciones.getText().toString().replaceAll("\n", ""));
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
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            d.dismiss();
                        }
                    });
            d = builder1.create();
            d.show();
        }else{
            if (spMuros.getSelectedItem().toString().equals(" ") &&
                spEstadoGeneral.getSelectedItem().toString().equals(" ") &&
                TextUtils.isEmpty(etObservaciones.getText().toString())
            ){
                etObservaciones.requestFocus();
                etObservaciones.setError("Al menos ingresar las observaciones de porque no se ha registrado ningun dato");
            }else {
                obtenerTipologia();
                try {
                    idRegistro = bdP.insertarLahares_ID(id_house,
                            cbReforzado.isChecked(),
                            spMuros.getSelectedItem().toString(),
                            spEstadoGeneral.getSelectedItem().toString(),
                            spTipLahares.getSelectedItem().toString(),
                            etObservaciones.getText().toString().replaceAll("\n", ""));

                    Toast toast;
                    toast=Toast.makeText(context,"Tipologia Lahares - Guardado Exitoso",Toast.LENGTH_SHORT);
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

    public void obtenerTipologia(){

        // Se comprueba si la vivienda cuenta con Refuerzo, si es asi se le asigna un valor de 0,1
        // de lo contrario un valor de 1, este valor se multiplica por el 50%, que es el porcentaje
        // de influencia en la tifipificacion estructural frente al evento volcanico Lahares
        if (cbReforzado.isChecked()){
            puntajeEst = (float) (0.1*0.5);
        }
        else {
            puntajeEst = (float) (1*0.5);
        }

        // Se le asigna un puntaje de Material de Muros que equivale al 30% en la tipificacion estructural
        // frente al evento volcanico Lahares
        //      Para Ladrillo Macizo se le asigna 0,1
        //      Para Bloque se le asigna 0,2
        //      Para Tapia Pisada se le asigna 0,5
        //      Para Bahareque se le asigna 0,8
        //      Para Madera se le asigna 1,0
        switch (spMuros.getSelectedItem().toString()){
            case "Ladrillo Macizo":
                puntajeMuros = (float) (0.1*0.3);
                break;
            case "Bloque":
                puntajeMuros = (float) (0.2*0.3);
                break;
            case "Tapia Pisada":
                puntajeMuros = (float) (0.5*0.3);
                break;
            case "Bahareque":
                puntajeMuros = (float) (0.8*0.3);
                break;
            case "Madera":
                puntajeMuros = (float) (1*0.3);
                break;
        }

        // Se le asigna un puntaje de acuerdo al Estado General de la Edificacion que equivale al 20% en
        // la tipificacion estructural frente al evento volcanico Lahares
        //      Para estado estructural Bueno se le asigna 0,1
        //      Para estado estructural Regular se le asigna 0,5
        //      Para estado estructural Malo se le asigna 1,0
        switch (spEstadoGeneral.getSelectedItem().toString()){
            case "Bueno":
                puntajeEstEd = (float) (0.1*0.2);
                break;
            case "Regular":
                puntajeEstEd = (float) (0.5*0.2);
                break;
            case "Malo":
                puntajeEstEd = (float) (1*0.2);
                break;
        }

        // Al sumar los resultados obtenidos del Refuerzo, el tipo de material de muro y el estado general de la edificacion
        // y despues de aplicar sus respectivos porcentajes, se obtiene el resultado que definira el tipo de Tipologia
        // Estructural frente a la amenaza volcanica Lahares que aplica para la vivienda
        resTipologia = puntajeEst + puntajeMuros + puntajeEstEd;

        // Se muestra en la interfaz los resultados obtenidos al realizar los calculos para determinar la tipologia
        etEst.setText(Float.toString(bdP.reducirFloat(puntajeEst)));
        etMuros.setText(Float.toString(bdP.reducirFloat(puntajeMuros)));
        etEstEd.setText(Float.toString(bdP.reducirFloat(puntajeEstEd)));
        etResTip.setText(Float.toString(bdP.reducirFloat(resTipologia)));

        // Tipologias
        // 0 -> Sin Tipologia
        // 1 -> Tipologia 1 => Si el resultado es mayor a 0,28
        // 2 -> Tipologia 2 => Si el resultado esta entre 0,18 y 0,28
        // 3 -> Tipologia 3 => Si el resultado es menor a 0,18
        // Una vez se define la Tipologia se muestra en la interfaz

        if (resTipologia < 0.18f){
            spTipLahares.setSelection(3);
        }
        else{
            if (resTipologia > 0.28f){
                spTipLahares.setSelection(1);
            }
            else{
                spTipLahares.setSelection(2);
            }
        }
    }

    public void cargarDatos(){
        bdP.abrirBD();
        Cursor cursorLahares = bdP.cargarDatos_ID_RID(id_house,"tbllahares");

        /*
        0 -> RowId
        1 -> IdCasa
        2 -> Reforzado
        3 -> Material Muros
        4 -> Estado Edificacion
        5 -> Tipologia Lahares
        6 -> Observaciones
         */

        if (cursorLahares.moveToFirst()){
            /*
            Tipos
            1 -> Material de Muros
            2 -> Estado General de la Edificacion
             */
            do {
                idRegistro = cursorLahares.getLong(0);

                if (cursorLahares.getInt(2)==1){
                    cbReforzado.setChecked(true);
                }

                setSpinner(cursorLahares.getString(3),1);
                setSpinner(cursorLahares.getString(4),2);

                etObservaciones.setText(cursorLahares.getString(6));

            }while (cursorLahares.moveToNext());
            obtenerTipologia();
        }
        bdP.cerrarBD();
    }

    public void setSpinner(String opc, int tipo){

        //Este metodo se encarga se asignar en la interfaz los valores que se descargan de la base de datos

        switch (tipo){
            case 1:
                switch (opc){
                    case " ":
                        spMuros.setSelection(0);
                        break;
                    case "Ladrillo Macizo":
                        spMuros.setSelection(1);
                        break;
                    case "Bloque":
                        spMuros.setSelection(2);
                        break;
                    case "Tapia Pisada":
                        spMuros.setSelection(3);
                        break;
                    case "Bahareque":
                        spMuros.setSelection(4);
                        break;
                    case "Madera":
                        spMuros.setSelection(5);
                        break;
                }
                break;
            case 2:
                switch (opc){
                    case " ":
                        spEstadoGeneral.setSelection(0);
                        break;
                    case "Bueno":
                        spEstadoGeneral.setSelection(1);
                        break;
                    case "Regular":
                        spEstadoGeneral.setSelection(2);
                        break;
                    case "Malo":
                        spEstadoGeneral.setSelection(3);
                        break;
                }
                break;
            case 3:
                switch (opc){
                    case "Tipologia_1":
                        spTipLahares.setSelection(1);
                        break;
                    case "Tipologia_2":
                        spTipLahares.setSelection(2);
                        break;
                    case "Tipologia_3":
                        spTipLahares.setSelection(3);
                        break;
                }
                break;
        }
    }
}