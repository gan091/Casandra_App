package com.android.gan091.gramaudenar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;

public class VentanaActivity extends AppCompatActivity implements View.OnClickListener {

    private ColorDrawable swipeBackground = new ColorDrawable(Color.parseColor("#EB1E1E"));
    private ColorDrawable swipeBackground2 = new ColorDrawable(Color.parseColor("#2196F3"));
    private Drawable iconDelete, iconUpdate;

    boolean existe = false;
    float areaLocal;
    int idCasa, posicion;
    long idRegistro = 0;

    Fuente objetoEliminado;
    Cursor cursorEliminado;
    AlertDialog d = null;
    AdaptadorVentana adaptador;
    ArrayList<Fuente> lista = new ArrayList<>();
    BaseDeDatos bdP;
    Context context;
    RecyclerView contenedor;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.right_in,R.anim.right_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ventana);
        Toolbar toolbar = findViewById(R.id.toolbarV);
        setSupportActionBar(toolbar);
        FloatingActionButton fabAdd = findViewById(R.id.fabAddVentana);
        FloatingActionButton fabExit = findViewById(R.id.fabExitVentana);
        Bundle bundle = getIntent().getExtras();

        idCasa = bundle.getInt("idCasa");
        context = this;
        bdP = new BaseDeDatos(context);

        contenedor = findViewById(R.id.contenedorVentana);
        contenedor.setHasFixedSize(true);
        LinearLayoutManager layout = new LinearLayoutManager(getApplicationContext());
        layout.setOrientation(LinearLayoutManager.VERTICAL);

        cargarDatos();

        adaptador = new AdaptadorVentana(lista);
        contenedor.setAdapter(adaptador);
        contenedor.setLayoutManager(layout);

        iconDelete = ContextCompat.getDrawable(this,R.drawable.ic_delete);
        iconUpdate = ContextCompat.getDrawable(this,R.drawable.ic_update);

        fabAdd.setOnClickListener(this);
        fabExit.setOnClickListener(this);

        loadSwipe();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.fabAddVentana:
                actualizarLista();
                break;
            case R.id.fabExitVentana:
                onBackPressed();
                break;
        }
    }

    public void cargarDatos(){

        bdP.abrirBD();
        final Cursor cursor = bdP.cargarDatosTablas(idCasa, "tblventana");

        try {
            if (cursor.moveToFirst()){
                existe = true;
                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);

                builder1.setTitle("Registro Existente")
                        .setMessage("Ya hay registros de Fachada de esta casa ¿Desea modificar los registros?")
                        .setPositiveButton("Si",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        inicializar(cursor);
                                    }
                                })
                        .setNegativeButton("No",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                });
                builder1.create().show();
            }
            else {
            }
        }
        catch (Exception e){
            Toast toast;
            toast=Toast.makeText(context,e.toString(),Toast.LENGTH_LONG);
            toast.show();
        }
        bdP.cerrarBD();
    }

    public void inicializar(Cursor cursor){
        /*
        0 -> RowId
        1 -> IdCasa
        2 -> Ancho
        3 -> Alto
        4 -> Area
        5 -> Numero de Piso
         */

        float ancho, alto;
        int numP;

        do {
            ancho = (float) cursor.getDouble(2);
            alto = (float) cursor.getDouble(3);
            numP = cursor.getInt(5);
            idRegistro = cursor.getLong(0);
            lista.add(new Fuente(ancho,alto,numP,idRegistro));
        }while (cursor.moveToNext());

        adaptador.notifyDataSetChanged();
    }

    public void loadSwipe(){

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return true;
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;
                int iconMargin = (itemView.getHeight() - iconDelete.getIntrinsicHeight()) / 2;
                if (dX>0){
                    swipeBackground.setBounds(
                            itemView.getLeft(),
                            itemView.getTop(),
                            (int)dX,
                            itemView.getBottom());
                    iconDelete.setBounds(
                            itemView.getLeft()+iconMargin,
                            itemView.getTop()+iconMargin,
                            itemView.getLeft()+iconMargin+iconDelete.getIntrinsicWidth(),
                            itemView.getBottom()-iconMargin);
                }else {
                    swipeBackground2.setBounds(
                            itemView.getRight()+(int)dX,
                            itemView.getTop(),
                            itemView.getRight(),
                            itemView.getBottom());
                    iconUpdate.setBounds(
                            itemView.getRight()-iconMargin-iconDelete.getIntrinsicWidth(),
                            itemView.getTop()+iconMargin,
                            itemView.getRight()-iconMargin,
                            itemView.getBottom()-iconMargin);
                }

                swipeBackground.draw(c);
                swipeBackground2.draw(c);

                c.save();

                if(dX > 0){
                    c.clipRect(
                            itemView.getLeft(),
                            itemView.getTop(),
                            (int)dX,
                            itemView.getBottom());
                }else {
                    c.clipRect(
                            itemView.getRight()+(int)dX,
                            itemView.getTop(),
                            itemView.getRight(),
                            itemView.getBottom());
                }

                iconDelete.draw(c);
                iconUpdate.draw(c);

                c.restore();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                //direccion = 4 --> Izquierda direccion = 8 --> Derecha

                posicion = viewHolder.getAdapterPosition();
                objetoEliminado = lista.get(posicion);

                idRegistro = lista.get(posicion).getIdRegistro();
                bdP.abrirBD();
                cursorEliminado = bdP.cargarDatos(idRegistro,"tblventana");
                cursorEliminado.moveToFirst();
                bdP.cerrarBD();

                if (direction == 8){
                    try {
                        bdP.eliminarRegistroExacto("tblventana",idRegistro);
                    }
                    catch (Exception e){
                        Toast toast;
                        toast=Toast.makeText(context,e.toString(),Toast.LENGTH_LONG);
                        toast.show();
                    }
                    lista.remove(posicion);
                    adaptador.notifyDataSetChanged();

                    Snackbar.make(viewHolder.itemView,"Registro eliminado",Snackbar.LENGTH_LONG).setAction("Deshacer", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                long registro = bdP.insertarVentana(
                                        cursorEliminado.getLong(0), //RowId
                                        cursorEliminado.getInt(1),  //IdCasa
                                        cursorEliminado.getFloat(2),//Ancho
                                        cursorEliminado.getFloat(3),//Alto
                                        cursorEliminado.getFloat(4),//Area
                                        cursorEliminado.getInt(5));//Numero de Piso

                                //Log.i("RowID recuperado-->",String.valueOf(registro));
                            }
                            catch (Exception e){
                                Toast toast;
                                toast=Toast.makeText(context,e.toString(),Toast.LENGTH_LONG);
                                toast.show();
                            }
                            lista.add(posicion,objetoEliminado);
                            adaptador.notifyDataSetChanged();
                        }
                    }).show();
                }else {
                    actualizarRegistro(
                            idRegistro,
                            objetoEliminado.getAncho(),
                            objetoEliminado.getAlto(),
                            objetoEliminado.getNumPiso(),
                            objetoEliminado);
                }
                adaptador.notifyDataSetChanged();
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(contenedor);
    }

    public long obtenerArea(float ancho, float alto, int numeroPiso){

        long rVentana=0;

        areaLocal = ancho * alto;

        try {
            rVentana = bdP.insertar(idCasa,ancho,alto,areaLocal,numeroPiso);
        }
        catch (Exception e){
            Toast toast;
            toast=Toast.makeText(context,e.toString(),Toast.LENGTH_LONG);
            toast.show();
        }
        return rVentana;
    }

    public void actualizarLista(){

        LayoutInflater layoutInflater = getLayoutInflater();
        View dialogLayout = layoutInflater.inflate(R.layout.form_ventana,null);

        final EditText etFVAncho = dialogLayout.findViewById(R.id.etFormAncho);
        final EditText etFVAlto = dialogLayout.findViewById(R.id.etFormAlto);
        final EditText etFVNumP = dialogLayout.findViewById(R.id.etFormNumeroP);
        final EditText etFVCantidad = dialogLayout.findViewById(R.id.etFormCantidad);

        Button btnFVAceptar = dialogLayout.findViewById(R.id.btnFormVAceptar);
        Button btnFVCancelar = dialogLayout.findViewById(R.id.btnFormVCancelar);

        final AlertDialog.Builder builder = new AlertDialog.Builder(VentanaActivity.this);
        builder.setView(dialogLayout);
        d = builder.create();

        btnFVAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int cantidad = Integer.parseInt(etFVCantidad.getText().toString());

                if (TextUtils.isEmpty(etFVAncho.getText().toString())){
                    etFVAncho.requestFocus();
                    etFVAncho.setError("El ancho de ventana no puede quedar vacio");
                    return;
                }
                else{
                    if(TextUtils.isEmpty(etFVAlto.getText().toString())){
                        etFVAlto.requestFocus();
                        etFVAlto.setError("El alto de ventana no puede quedar vacio");
                        return;
                    }else{
                        if (TextUtils.isEmpty(etFVNumP.getText().toString())){
                            etFVNumP.requestFocus();
                            etFVNumP.setError("El numero de piso de ventana no puede quedar vacio");
                            return;
                        }else {
                            float anchoRegistro = Float.parseFloat(etFVAncho.getText().toString());
                            float altoRegistro = Float.parseFloat(etFVAlto.getText().toString());
                            int numeroPisoRegistro = Integer.parseInt(etFVNumP.getText().toString());

                            for (int i=0;i<cantidad;i++){
                                long registro = obtenerArea(anchoRegistro,altoRegistro,numeroPisoRegistro);
                                //Log.i("RowID Guardar-->",String.valueOf(registro));
                                lista.add(new Fuente(anchoRegistro,altoRegistro,numeroPisoRegistro,registro));
                            }
                        }
                    }
                }
                adaptador.notifyDataSetChanged();
                d.dismiss();
            }
        });

        btnFVCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                d.dismiss();
            }
        });

        d.show();
    }

    public void actualizarRegistro(final long idR, float ancho, float alto, int nP, final Fuente oE){

        LayoutInflater layoutInflater = getLayoutInflater();
        View dialogLayout = layoutInflater.inflate(R.layout.form_ventana,null);

        final EditText etFVAncho = dialogLayout.findViewById(R.id.etFormAncho);
        final EditText etFVAlto = dialogLayout.findViewById(R.id.etFormAlto);
        final EditText etFVNumP = dialogLayout.findViewById(R.id.etFormNumeroP);
        final EditText etFVCantidad = dialogLayout.findViewById(R.id.etFormCantidad);

        Button btnFVAceptar = dialogLayout.findViewById(R.id.btnFormVAceptar);
        Button btnFVCancelar = dialogLayout.findViewById(R.id.btnFormVCancelar);

        final AlertDialog.Builder builder = new AlertDialog.Builder(VentanaActivity.this);
        builder.setView(dialogLayout);
        d = builder.create();

        etFVAncho.setText(Float.toString(ancho));
        etFVAlto.setText(Float.toString(alto));
        etFVNumP.setText(Integer.toString(nP));
        etFVCantidad.setVisibility(View.INVISIBLE);
        btnFVAceptar.setText("Actualizar");

        btnFVAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(etFVAncho.getText().toString())) {
                    etFVAncho.requestFocus();
                    etFVAncho.setError("El ancho de ventana no puede quedar vacio");
                    return;
                } else {
                    if (TextUtils.isEmpty(etFVAlto.getText().toString())) {
                        etFVAlto.requestFocus();
                        etFVAlto.setError("El alto de ventana no puede quedar vacio");
                        return;
                    } else {
                        if (TextUtils.isEmpty(etFVNumP.getText().toString())) {
                            etFVNumP.requestFocus();
                            etFVNumP.setError("El numero de piso de ventana no puede quedar vacio");
                            return;
                        } else {

                            float anchoRegistro = Float.parseFloat(etFVAncho.getText().toString());
                            float altoRegistro = Float.parseFloat(etFVAlto.getText().toString());
                            int numPisoRegistro = Integer.parseInt(etFVNumP.getText().toString());
                            areaLocal = anchoRegistro * altoRegistro;

                            bdP.abrirBD();
                            try {
                                bdP.updateVentana(idR,anchoRegistro,altoRegistro,numPisoRegistro,areaLocal);
                            }
                            catch (Exception e){
                                Toast toast;
                                toast=Toast.makeText(context,e.toString(),Toast.LENGTH_LONG);
                                toast.show();
                            }
                            bdP.cerrarBD();

                            oE.setAncho(anchoRegistro);
                            oE.setAlto(altoRegistro);
                            oE.setNumPiso(numPisoRegistro);
                        }
                    }
                }
                adaptador.notifyDataSetChanged();
                d.dismiss();
            }
        });

        btnFVCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                d.dismiss();
            }
        });

        d.show();
    }
}