package com.android.gan091.gramaudenar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by GAN091 on 23/10/2017.
 */

public class BaseDeDatos extends SQLiteOpenHelper {

    private boolean inicio = true;
    private Context contextLocal;
    private Cursor cursor;
    private SQLiteDatabase db;

    public BaseDeDatos(Context context) {
        super(context, "GramaDB", null, 1);
        contextLocal = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //this.sqLiteDatabase = sqLiteDatabase;

        String sqlTablaCasa = "CREATE TABLE tblcasa" +
                "(idcasa VARCHAR(30) PRIMARY KEY, " +
                "latitud VARCHAR(20), " +
                "longitud VARCHAR(20), " +
                "lugar VARCHAR(30))";

        String sqlTablaZona = "CREATE TABLE tblzona" +
                "(id INTEGER PRIMARY KEY, " +
                "corregimiento VARCHAR(11), " +
                "sector INTEGER)";

        String sqlTablaCamara = "CREATE TABLE tblcamara" +
                "(id INTEGER PRIMARY KEY, " +
                "latitud VARCHAR(20), " +
                "longitud VARCHAR(20))";

        String sqlTablaOndaChoqueEnterramiento = "CREATE TABLE tblondachoque" +
                "(idcasa VARCHAR(30), " +
                "materialventana VARCHAR(7), " +
                "marcoventana VARCHAR(15), " +
                "materialpiso VARCHAR(8), " +
                "materialmuros VARCHAR(13), " +
                "tipologiaonda VARCHAR(12), " +
                "tipologiaent VARCHAR(12), " +
                "observacionesoch VARCHAR(200))";

        String sqlTablaFachada = "CREATE TABLE tblfachada" +
                "(idcasa VARCHAR(30), " +
                "ancho FLOAT, " +
                "altura FLOAT, " +
                "area FLOAT, " +
                "areapiso1 FLOAT)";

        String sqlTablaFachada1 = "CREATE TABLE tblfachada1" +
                "(idcasa VARCHAR(30), " +
                "anchofg FLOAT, " +
                "altofg FLOAT, " +
                "areafg FLOAT, " +
                "areatotalfachada FLOAT)";

        String sqlTablaVentana = "CREATE TABLE tblventana" +
                "(idcasa VARCHAR(30), " +
                "ancho FLOAT, " +
                "altura FLOAT, " +
                "area FLOAT, " +
                "numeropiso INTEGER)";

        String sqlTablaVentana1 = "CREATE TABLE tblventana1" +
                "(idcasa VARCHAR(30), " +
                "anchovg FLOAT, " +
                "altovg FLOAT, " +
                "areavg FLOAT, " +
                "npisovg INTEGER, " +
                "anchovp FLOAT, " +
                "altovp FLOAT, " +
                "areavp FLOAT, " +
                "npisovp INTEGER, " +
                "areatotalventanas FLOAT, " +
                "porcentajeaberturas FLOAT)";

        String sqlTablaPuerta = "CREATE TABLE tblpuerta" +
                "(idcasa VARCHAR(30), " +
                "ancho FLOAT, " +
                "altura FLOAT, " +
                "area FLOAT)";

        String sqlTablaPuerta1 = "CREATE TABLE tblpuerta1" +
                "(idcasa VARCHAR(30), " +
                "anchopg FLOAT, " +
                "alturapg FLOAT, " +
                "areapg FLOAT, " +
                "areatotalpuertas FLOAT)";

        String sqlTablaLahares = "CREATE TABLE tbllahares" +
                "(idcasa VARCHAR(30), " +
                "reforzado BOOL, " +
                "materialmuros VARCHAR(16), " +
                "estadoedificacion VARCHAR(8), " +
                "tipologialahares VARCHAR(12), " +
                "observacionesl VARCHAR(200))";

        String sqlTablaCeniza = "CREATE TABLE tblceniza" +
                "(idcasa VARCHAR(30), " +
                "tipotecho VARCHAR(9), " +
                "materialcobertura VARCHAR(16), " +
                "materialapoyo VARCHAR(16), " +
                "formacubierta VARCHAR(13), " +
                "inclinacioncubierta VARCHAR(20), " +
                "estadogeneralcubierta VARCHAR(8), " +
                "tipologiaceniza VARCHAR(12), " +
                "observacionesc VARCHAR(200))";

        String sqlTablaTutorial = "CREATE TABLE tbltutorial(visto BOOL)";

        String sqlTablaUsuarios = "CREATE TABLE tblusuarios" +
                "(iduser INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user TEXT," +
                "pass TEXT)";

        sqLiteDatabase.execSQL(sqlTablaFachada);
        sqLiteDatabase.execSQL(sqlTablaFachada1);
        sqLiteDatabase.execSQL(sqlTablaVentana);
        sqLiteDatabase.execSQL(sqlTablaVentana1);
        sqLiteDatabase.execSQL(sqlTablaPuerta);
        sqLiteDatabase.execSQL(sqlTablaPuerta1);
        sqLiteDatabase.execSQL(sqlTablaOndaChoqueEnterramiento);
        sqLiteDatabase.execSQL(sqlTablaCasa);
        sqLiteDatabase.execSQL(sqlTablaLahares);
        sqLiteDatabase.execSQL(sqlTablaCeniza);
        sqLiteDatabase.execSQL(sqlTablaZona);
        sqLiteDatabase.execSQL(sqlTablaCamara);
        sqLiteDatabase.execSQL(sqlTablaTutorial);
        sqLiteDatabase.execSQL(sqlTablaUsuarios);

        if (inicio){
            sqLiteDatabase.execSQL("INSERT INTO tblusuarios (iduser, user, pass) VALUES (1,'root','root')");
            sqLiteDatabase.execSQL("INSERT INTO tblzona (id, corregimiento, sector) VALUES (1,'Rural',0)");
            sqLiteDatabase.execSQL("INSERT INTO tbltutorial (visto) VALUES (0)");
            sqLiteDatabase.execSQL("INSERT INTO tblcamara (id, latitud, longitud) VALUES (1,'1.299568021668017','-77.39873830229044')");
            inicio = false;
        }
    }

    public void abrirBD(){
        BaseDeDatos bdP;
        bdP = new BaseDeDatos(contextLocal);
        db = bdP.getWritableDatabase();
    }

    public Cursor cargarDatos(String tabla){
        cursor = db.rawQuery("SELECT * FROM "+tabla,null);
        return cursor;
    }

    Cursor cargarDatosFinales(){
        Cursor cursor = db.rawQuery
                ("select " +
                                            "tblcasa.idcasa, " +
                                            "tblcasa.latitud, " +
                                            "tblcasa.longitud, " +
                                            "tblcasa.lugar, " +
                                            "tblceniza.tipologiaceniza, " +
                                            "tblceniza.estadogeneralcubierta, " +
                                            "tblceniza.tipotecho, " +
                                            "tblceniza.materialcobertura, " +
                                            "tblceniza.materialapoyo, " +
                                            "tblceniza.formacubierta, " +
                                            "tblceniza.inclinacioncubierta, " +
                                            "tblfachada1.anchofg, " +
                                            "tblfachada1.altofg, " +
                                            "tblfachada1.areafg, " +
                                            "tblfachada1.areatotalfachada, " +
                                            "tblventana1.anchovg, " +
                                            "tblventana1.altovg, " +
                                            "tblventana1.areavg, " +
                                            "tblventana1.npisovg, " +
                                            "tblventana1.anchovp, " +
                                            "tblventana1.altovp, " +
                                            "tblventana1.areavp, " +
                                            "tblventana1.npisovp, " +
                                            "tblventana1.areatotalventanas, " +
                                            "tblpuerta1.anchopg, " +
                                            "tblpuerta1.alturapg, " +
                                            "tblpuerta1.areapg, " +
                                            "tblpuerta1.areatotalpuertas, " +
                                            "tblondachoque.materialventana, " +
                                            "tblondachoque.marcoventana, " +
                                            "tblondachoque.materialpiso, " +
                                            "tblondachoque.materialmuros, " +
                                            "tblondachoque.tipologiaonda, " +
                                            "tblondachoque.tipologiaent, " +
                                            "tblondachoque.observacionesoch, " +
                                            "tbllahares.tipologialahares, " +
                                            "tbllahares.reforzado, " +
                                            "tbllahares.materialmuros, " +
                                            "tbllahares.estadoedificacion, " +
                                            "tbllahares.observacionesl " +
                                    "from " +
                                            "tblcasa " +
                                    "left join " +
                                            "tbllahares " +
                                            "on " +
                                                "tblcasa.idcasa = tbllahares.idcasa " +
                                    "left join " +
                                            "tblondachoque " +
                                            "on " +
                                                "tblcasa.idcasa = tblondachoque.idcasa " +
                                    "left join " +
                                            "tblceniza " +
                                            "on " +
                                                "tblcasa.idcasa = tblceniza.idcasa " +
                                    "left join " +
                                            "tblfachada1 " +
                                            "on " +
                                                "tblcasa.idcasa = tblfachada1.idcasa " +
                                    "left join " +
                                            "tblventana1 " +
                                            "on " +
                                                "tblcasa.idcasa = tblventana1.idcasa " +
                                    "left join " +
                                            "tblpuerta1 " +
                                            "on " +
                                                "tblcasa.idcasa = tblpuerta1.idcasa"
                ,null);
        return cursor;
    }

    Cursor cargarDatosVentanaP1_RID(String idCasa){
        cursor = db.rawQuery("SELECT ROWID, * FROM tblVentana" +" WHERE idcasa="+"'"+idCasa+"' "+" AND numeropiso=1",null);
        return cursor;
    }

    Cursor cargarDatos_ID_RID(String idCasa, String tabla){
        cursor = db.rawQuery("SELECT ROWID, * FROM "+tabla+" WHERE idcasa="+"'"+idCasa+"'",null);
        return cursor;
    }

    Cursor cargarDatos_RID(long rowId, String tabla){
        cursor = db.rawQuery("SELECT ROWID, * FROM "+tabla+" WHERE ROWID="+rowId,null);
        return cursor;    }

    Cursor cargarDatos2P(String parametro1, String parametro2, String tabla){
        cursor = db.rawQuery("SELECT "+parametro1+", "+parametro2+" FROM "+tabla,null);
        return cursor;
    }

    //Verficar si ya no se utiliza este metodo
    Cursor cargarDatos2P_ID(String parametro1, String parametro2, String tabla, String idCasa){
        String sql = "SELECT "+parametro1+", "+parametro2+" FROM "+tabla+" WHERE idcasa="+"'"+idCasa+"'";
        cursor = db.rawQuery(sql,null);
        return cursor;
    }

    Cursor cargarDatos3P_ID(String parametro1, String parametro2, String parametro3, String tabla, String idCasa){
        String sql = "SELECT "+parametro1+", "+parametro2+", "+parametro3+" FROM "+tabla+" WHERE idcasa="+"'"+idCasa+"'";
        cursor = db.rawQuery(sql,null);
        return cursor;
    }

    public void cerrarBD(){
        db.close();
    }

    void eliminarRegistro_ID(String tabla, String idCasa) {
        abrirBD();
        db.execSQL("DELETE FROM "+tabla+" WHERE idcasa="+"'"+idCasa+"'");
        cerrarBD();
    }

    void eliminarRegistro_RID(String tabla, long rowId) throws Exception{
        abrirBD();
        db.execSQL("DELETE FROM "+tabla+" WHERE ROWID="+rowId);
        cerrarBD();
    }

    public void eliminarUsuario(int idUsuario){
        abrirBD();
        db.execSQL("DELETE FROM tblusuarios WHERE iduser="+idUsuario);
        cerrarBD();
    }

    boolean existeRegistro_ID(String tabla, String idCasa) {
        abrirBD();
        boolean existe = false;
        if (db != null) {
            cursor = db.rawQuery("SELECT * FROM "+tabla+" WHERE idcasa="+"'"+idCasa+"'", null);
            cursor.moveToFirst();

            if (cursor.getCount() > 0) {
                existe = true;
            }
        }cerrarBD();
        return existe;
    }

    boolean existeRegistro_RID(long idR, String tabla) {
        abrirBD();
        boolean existe = false;
        if (db != null) {
            cursor = db.rawQuery("SELECT * FROM "+tabla+" WHERE ROWID="+idR, null);

            if (cursor.moveToFirst()) {
                existe = true;
            }
        }
        cerrarBD();
        return existe;
    }

    String getIdCasa(String lat, String lon){
        String iC="";
        abrirBD();
        cursor = db.rawQuery("SELECT idcasa FROM tblCasa WHERE latitud='"+lat+"' AND longitud='"+lon+"'",null);
        if (cursor.moveToFirst()){
            iC = cursor.getString(0);
        }
        cerrarBD();
        return iC;
    }

    void insertarCamara(String lat, String lon) throws Exception{
        abrirBD();
        ContentValues values = new ContentValues();
        values.put("latitud",lat);
        values.put("longitud",lon);
        db.update("tblcamara",values,"id=1",null);
        cerrarBD();
    }

    long insertarCasa(String id, String lat, String lon, String zona) throws Exception{
        ContentValues values = new ContentValues();
        values.put("idcasa",id);
        values.put("latitud",lat);
        values.put("longitud",lon);
        values.put("lugar",zona);

        return db.insert("tblcasa",null,values);
    }

    long insertarCeniza_ID(String idCasa, String tipoTecho, String matCob, String matElemApoyo, String formaCubierta, String inclCub, String estadoGen, String tipCeniza, String obvs) throws Exception{
        abrirBD();
        long registro = 0;

        if(db!=null){
            ContentValues nuevoRegistro = new ContentValues();
            nuevoRegistro.put("idcasa",idCasa);
            nuevoRegistro.put("tipoTecho",tipoTecho);
            nuevoRegistro.put("materialcobertura",matCob);
            nuevoRegistro.put("materialapoyo",matElemApoyo);
            nuevoRegistro.put("formacubierta",formaCubierta);
            nuevoRegistro.put("inclinacioncubierta",inclCub);
            nuevoRegistro.put("estadogeneralcubierta",estadoGen);
            nuevoRegistro.put("tipologiaceniza",tipCeniza);
            nuevoRegistro.put("observacionesc",obvs);

            registro = db.insert("tblceniza",null,nuevoRegistro);
        }
        cerrarBD();
        return registro;
    }

    long insertarFachada_ID(String idCasa, float anchoF, float alturaF, float areaF, float areaFP1) throws Exception{
        abrirBD();
        long registro=0;
        if(db!=null){
            ContentValues values = new ContentValues();
            values.put("idcasa",idCasa);
            values.put("ancho",anchoF);
            values.put("altura",alturaF);
            values.put("area",areaF);
            values.put("areapiso1",areaFP1);

            registro = db.insert("tblfachada",null,values);
        }
        cerrarBD();
        return registro;
    }

    long insertarFachada_RID(long rowId, String idCasa, float anchoF, float alturaF, float areaF, float areaFP1) throws Exception{
        abrirBD();
        long registro=0;
        if(db!=null){
            ContentValues values = new ContentValues();
            values.put("ROWID",rowId);
            values.put("idcasa",idCasa);
            values.put("ancho",anchoF);
            values.put("altura",alturaF);
            values.put("area",areaF);
            values.put("areapiso1",areaFP1);

            registro = db.insert("tblfachada",null,values);
        }
        cerrarBD();
        return registro;
    }

    long insertarFachada1_ID(String idCasa, float anchoFG, float alturaFG, float areaFG, float areaTFG) throws Exception{
        long registro=0;

        if(db!=null){
            ContentValues nuevoRegistro = new ContentValues();
            nuevoRegistro.put("idcasa",idCasa);
            nuevoRegistro.put("anchofg",anchoFG);
            nuevoRegistro.put("altofg",alturaFG);
            nuevoRegistro.put("areafg",areaFG);
            nuevoRegistro.put("areatotalfachada",areaTFG);

            registro = db.insert("tblfachada1",null,nuevoRegistro);
        }
        return registro;
    }

    long insertarLahares_ID(String idCasa, boolean reforzado, String matMur, String estGeneral, String tipLahares, String obvs) throws Exception{
        abrirBD();
        long registro = 0;

        if(db!=null){
            ContentValues nuevoRegistro = new ContentValues();
            nuevoRegistro.put("idcasa",idCasa);
            nuevoRegistro.put("reforzado",reforzado);
            nuevoRegistro.put("materialmuros",matMur);
            nuevoRegistro.put("estadoedificacion",estGeneral);
            nuevoRegistro.put("tipologialahares",tipLahares);
            nuevoRegistro.put("observacionesl",obvs);

            registro = db.insert("tbllahares",null,nuevoRegistro);
        }
        cerrarBD();
        return registro;
    }

    long insertarOndaChoqueEnterramiento_ID(String idCasa, String matVen, String marVen, String matPiso, String matMur, String tipOndaCh, String tipEnt, String obvs) throws Exception{
        abrirBD();
        long registro = 0;

        if(db!=null){
            ContentValues values = new ContentValues();
            values.put("idcasa",idCasa);
            values.put("materialventana",matVen);
            values.put("marcoventana",marVen);
            values.put("materialpiso",matPiso);
            values.put("materialmuros",matMur);
            values.put("tipologiaonda",tipOndaCh);
            values.put("tipologiaent",tipEnt);
            values.put("observacionesoch",obvs);

            registro = db.insert("tblondachoque",null,values);
        }
        cerrarBD();
        return registro;
    }

    long insertarPuerta_ID(String idCasa, float anchoP, float alturaP, float areaP) throws Exception{
        abrirBD();
        long registro=0;

        if(db!=null){
            ContentValues nuevoRegistro = new ContentValues();
            nuevoRegistro.put("idcasa",idCasa);
            nuevoRegistro.put("ancho",anchoP);
            nuevoRegistro.put("altura",alturaP);
            nuevoRegistro.put("area",areaP);

            registro = db.insert("tblpuerta",null,nuevoRegistro);
        }
        cerrarBD();
        return registro;
    }

    long insertarPuerta_RID(long rowId, String idCasa, float anchoP, float alturaP, float areaP) throws Exception{
        abrirBD();
        long registro=0;
        if(db!=null){
            ContentValues values = new ContentValues();
            values.put("ROWID",rowId);
            values.put("idcasa",idCasa);
            values.put("ancho",anchoP);
            values.put("altura",alturaP);
            values.put("area",areaP);

            registro = db.insert("tblpuerta",null,values);
        }
        cerrarBD();
        return registro;
    }

    long insertarPuerta1_ID(String idCasa, float anchoPG, float alturaPG, float areaPG, float areaTPG) throws Exception{
        long registro=0;

        if(db!=null){
            ContentValues nuevoRegistro = new ContentValues();
            nuevoRegistro.put("idcasa",idCasa);
            nuevoRegistro.put("anchopg",anchoPG);
            nuevoRegistro.put("alturapg",alturaPG);
            nuevoRegistro.put("areapg",areaPG);
            nuevoRegistro.put("areatotalpuertas",areaTPG);

            registro = db.insert("tblpuerta1",null,nuevoRegistro);
        }
        return registro;
    }

    public boolean insertarUsuario(String u, String p){
        abrirBD();
        long registro = 0;

        ContentValues values = new ContentValues();
        values.put("user",u);
        values.put("pass",p);

        registro = db.insert("tblusuarios",null,values);
        cerrarBD();
        return (registro>0);
    }

    long insertarVentana_ID(String idCasa, float anchoV, float altoV, float areaV, int numPiso) throws Exception{
        abrirBD();
        long registro=0;

        if(db!=null){
            ContentValues nuevoRegistro = new ContentValues();
            nuevoRegistro.put("idcasa",idCasa);
            nuevoRegistro.put("ancho",anchoV);
            nuevoRegistro.put("altura",altoV);
            nuevoRegistro.put("area",areaV);
            nuevoRegistro.put("numeropiso",numPiso);

            registro = db.insert("tblventana",null,nuevoRegistro);
        }
        cerrarBD();
        return registro;
    }

    long insertarVentana_RID(long rowId, String idCasa, float anchoV, float alturaV, float areaV, int numPiso) throws Exception{
        abrirBD();
        long registro=0;
        if(db!=null){
            ContentValues values = new ContentValues();
            values.put("ROWID",rowId);
            values.put("idcasa",idCasa);
            values.put("ancho",anchoV);
            values.put("altura",alturaV);
            values.put("area",areaV);
            values.put("numeropiso",numPiso);

            registro = db.insert("tblventana",null,values);
        }
        cerrarBD();
        return registro;
    }

    long insertarVentana1_ID(String idCasa, float anchoVG, float alturaVG, float areaVG, int nPisoVG, float anchoVP, float alturaVP, float areaVP, int nPisoVP, float areaTotalVen, float porcAb) throws Exception{
        long registro=0;

        if(db!=null){
            ContentValues nuevoRegistro = new ContentValues();
            nuevoRegistro.put("idcasa",idCasa);
            nuevoRegistro.put("anchovg",anchoVG);
            nuevoRegistro.put("altovg",alturaVG);
            nuevoRegistro.put("areavg",areaVG);
            nuevoRegistro.put("npisovg",nPisoVG);
            nuevoRegistro.put("anchovp",anchoVP);
            nuevoRegistro.put("altovp",alturaVP);
            nuevoRegistro.put("areavp",areaVP);
            nuevoRegistro.put("npisovp",nPisoVP);
            nuevoRegistro.put("areatotalventanas",areaTotalVen);
            nuevoRegistro.put("porcentajeaberturas",porcAb);

            registro = db.insert("tblventana1",null,nuevoRegistro);
        }
        return registro;
    }

    void insertarZona(String corregimiento, int sector) throws Exception{
        abrirBD();
        ContentValues values = new ContentValues();
        values.put("corregimiento",corregimiento);
        values.put("sector",sector);
        db.update("tblzona",values,"id=1",null);
        cerrarBD();
    }

    void limpiarTabla(String tabla) throws Exception{
        db.execSQL("DELETE FROM "+ tabla);
    }

    void updateCaidadeCeniza_RID(long rowId, String tipoTecho, String materialCobertura, String materialElementoApoyo, String formaCubierta, String inclinacionCubierta, String estadoGeneral, String tipologiaCeniza, String obs) throws Exception{

        ContentValues values = new ContentValues();
        values.put("tipotecho",tipoTecho);
        values.put("materialcobertura",materialCobertura);
        values.put("materialapoyo",materialElementoApoyo);
        values.put("formacubierta",formaCubierta);
        values.put("inclinacioncubierta",inclinacionCubierta);
        values.put("estadogeneralcubierta",estadoGeneral);
        values.put("tipologiaceniza",tipologiaCeniza);
        values.put("observacionesc",obs);
        db.update("tblceniza",values,"ROWID="+rowId,null);
    }

    void updateCeniza_ID(String idCasa, String tipCen, String tabla) throws Exception{
        ContentValues values = new ContentValues();
        values.put("tipologiaceniza",tipCen);
        db.update(tabla,values,"idcasa="+"'"+idCasa+"'",null);
    }

    void updateFachada_RID(long rowId, float ancho, float area, float area1) throws Exception{
        ContentValues values = new ContentValues();
        values.put("ancho",ancho);
        values.put("area",area);
        values.put("areapiso1",area1);
        db.update("tblfachada",values,"ROWID="+rowId,null);
    }

    void updateLahares_ID(String idCasa, String tipLah, String tabla) throws Exception{
        ContentValues values = new ContentValues();
        values.put("tipologialahares",tipLah);
        db.update(tabla,values,"idcasa="+"'"+idCasa+"'",null);
    }

    void updateLahares_RID(long rowId, boolean refuerzo, String materialMuros, String estadoGeneral, String tipologiaLahares, String obs) throws Exception{

        ContentValues values = new ContentValues();
        values.put("reforzado",refuerzo);
        values.put("materialmuros",materialMuros);
        values.put("estadoedificacion",estadoGeneral);
        values.put("tipologialahares",tipologiaLahares);
        values.put("observacionesl",obs);
        db.update("tbllahares",values,"ROWID="+rowId,null);
    }

    void updateOndadeChoque_RID(long rowId, String matVen, String marVen, String matPiso, String tipOndaCh, String tipEnt, String obs) throws Exception{
        ContentValues values = new ContentValues();
        values.put("materialventana",matVen);
        values.put("marcoventana",marVen);
        values.put("materialpiso",matPiso);
        values.put("tipologiaonda",tipOndaCh);
        values.put("tipologiaent",tipEnt);
        values.put("observacionesoch",obs);
        db.update("tblondachoque",values,"ROWID="+rowId,null);
    }

    void updatePuerta_RID(long rowId, float ancho, float alto, float area) throws Exception{
        ContentValues values = new ContentValues();
        values.put("ancho",ancho);
        values.put("altura",alto);
        values.put("area",area);
        db.update("tblpuerta",values,"ROWID="+rowId,null);
    }

    void updateTutorial(int valor) {
        ContentValues values = new ContentValues();
        values.put("visto",valor);
        db.update("tbltutorial",values,"ROWID=1",null);
    }

    public void updateUsuario(int id, String u, String p) throws Exception {
        abrirBD();
        ContentValues values = new ContentValues();
        values.put("user",u);
        values.put("pass",p);
        db.update("tblusuarios",values,"iduser="+id,null);
        cerrarBD();
    }

    void updateVentana_RID(long rowId, float ancho, float alto, int numP, float area) throws Exception{
        ContentValues values = new ContentValues();
        values.put("ancho",ancho);
        values.put("altura",alto);
        values.put("area",area);
        values.put("numeropiso",numP);
        db.update("tblventana",values,"ROWID="+rowId,null);
    }

    float reducirFloat(float f){
        float f1 = (float)(Math.round(f*100.0)/100.0);
        return f1;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}