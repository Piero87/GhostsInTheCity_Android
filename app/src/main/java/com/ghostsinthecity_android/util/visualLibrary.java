package com.ghostsinthecity_android.util;

import com.ghostsinthecity_android.models.Point;

import java.util.ArrayList;

/**
 * Created by andreabuscarini on 04/03/16.
 */
public class visualLibrary {

    private static ArrayList<Point> visualTriangle;

    public static double TWOPI = 2*Math.PI;

    public static double getOrientation(Point actualPosition, Point lastPosition) {
        double dLong = actualPosition.getLongitude() - lastPosition.getLongitude();
        //System.out.println("@@@ DEBUG Player.getOrientation() - dLong = "+dLong);

        double y = Math.sin(dLong) * Math.cos(actualPosition.getLatitude());
        double x = Math.cos(lastPosition.getLatitude()) * Math.sin(actualPosition.getLatitude()) - Math.sin(lastPosition.getLatitude())*Math.cos(actualPosition.getLatitude()) * Math.cos(dLong);

        double bearing = Math.atan2(y, x) * (180 / Math.PI);

        if (bearing < 0) bearing +=360;

        return bearing;
    }

    public static double fromDegreesToRadians(double degrees){
        double radians = (degrees * Math.PI) / 180;

        return radians;
    }

    public static double fromRadiansToDegree(double radians){
        double degree = (radians * 180)/Math.PI;

        return degree;
    }

    //formula per trovare le coordinate a partire da un punto (coordianate espresse in radianti)
    // distanti distanceInMeter metri e con un angolo pari a bearing
    private static Point getCoordinatesFarFromPlayer(double lat, double lon, double bearing, double distance){

        //System.out.println("@@@ DEBUG lat: "+lat+" lon: "+lon+" bearing: "+bearing+" distInKM: "+distance);

        double latVertex;
        double lonVertex;
        double earthRadius = 6371000;

        latVertex = Math.asin(Math.sin(lat)*Math.cos(distance/earthRadius)+Math.cos(lat)*Math.sin(distance/earthRadius)*Math.cos(bearing));

        lonVertex = lon + Math.atan2(Math.sin(bearing)*Math.sin(distance/earthRadius)*Math.cos(lat), Math.cos(distance/earthRadius)-Math.sin(lat)*Math.sin(latVertex));

        Point vertex = new Point();
        vertex.setLatitude(fromRadiansToDegree(latVertex));
        vertex.setLongitude(fromRadiansToDegree(lonVertex));

        return vertex;
    }

    //metodo che restituisce un arraylist con le 2 coordinate dei punti del triangolo del campo visivo umano
    public static ArrayList<Point> getVisualTriangle(Point pos, double bearing){

        Point vertexRight;
        Point vertexLeft;

        //attuale posizione del player e vertice da cui partono le direttrici del campo visivo
        Point startCoordinates = pos;

        //angolo iniziale
        double startBearing = bearing;

        //Supponiamo che l'angolo di campo visivo sia di 50 gradi, quindi sara l'angolo iniziale +25 gradi per il
        //vertice a destra, e l'angolo iniziale + (360-25) gradi per il vertice a sinistra

        //angolo a cui sarà il vertice destro del campo visivo
        double bearing25degrees = (startBearing+25)%360;
        //System.out.println("@@@ DEBUG bearing45= "+bearing25degrees);

        //angolo a cui sarà il vertice sinistro del campo visivo
        double bearing335degrees = (startBearing+335)%360;
        //System.out.println("@@@ DEBUG bearing315= "+bearing335degrees);


        //distanza in metri a cui posizionare le coordinate del campo visivo a partire dalla posizione
        //del player, cioè a quanto vogliamo che veda il player tutti le entità del sistema
        double distanceInMeter = 50;

        double radiansLatitude = fromDegreesToRadians(startCoordinates.getLatitude());
        double radiansLongitude = fromDegreesToRadians(startCoordinates.getLongitude());

        vertexRight = getCoordinatesFarFromPlayer(radiansLatitude, radiansLongitude, bearing25degrees, distanceInMeter);
        vertexLeft = getCoordinatesFarFromPlayer(radiansLatitude, radiansLongitude, bearing335degrees, distanceInMeter);

        visualTriangle = new ArrayList<Point>();
        visualTriangle.add(vertexRight);
        visualTriangle.add(vertexLeft);

        System.out.println("@@@ DEBUG vertexRight: (" + vertexRight.getLatitude() + ", " + vertexRight.getLongitude() + ")");
        System.out.println("@@@ DEBUG vertexLeft: (" + vertexLeft.getLatitude()+", "+vertexLeft.getLongitude()+")");

        return visualTriangle;

    }

    /*
    * Metodo che restituisce true se le coordinate passate sono all'interno del cono visuale del Player
    * se le coordinate passate sono esterne restituisce false.
    * L'idea è di creare il poligono (tringolo) di visuale del player, partendo dalla sua posizione
    * e aggiungendo i due vertici a 45° e 315° (in modo da avere una campo visivo che simuli quello reale
    * di 45°) in base all'angolo di orientamento iniziale (questo perchè del player conosco le coordinate,
    * ma non dove sta guardando, e questo è fondamentale per sapere se un entità del sistema è nella sua visuale
    * quindi visibile al player o meno).
    * Una volta trovato il campo visivo del player (tutti i metodi per crearlo sono nella classe player)
    * allora per ongi entità il sistema mi restituirà le coordinate e io andrò a controllare se sono o meno
    * dentro al campo visivo del player per poterle mostrare sullo schermo
    * Fai la somma degli angoli compresi tra il punto di test ed ogni coppia di punti che compongono il poligono.
    * Se la somma è 2PI allora il punto è interno, se 0 il punto è esterno.
    */
    public static boolean coordinates_is_inside_the_polygon(ArrayList<Point> visual, Point p){
        double angle=0;
        double p1_lat;
        double p1_long;
        double p2_lat;
        double p2_long;

        for (int i=0;i<visual.size();i++) {
            p1_lat = visual.get(i).getLatitude() - p.getLatitude();
            p1_long = visual.get(i).getLongitude() - p.getLongitude();

            p2_lat = visual.get((i+1)%visual.size()).getLatitude() - p.getLatitude();
            p2_long = visual.get((i+1)%visual.size()).getLongitude() - p.getLongitude();

            angle += angle2D(p1_lat, p1_long, p2_lat, p2_long);
        }

        if (Math.abs(angle) < Math.PI){
            return false;
        }else{
            return true;
        }

    }

    /*
    * Ritorna l'angolo tra 2 vettori in un piano. L'angolo è da il vettore 1 al vettore 2, positivo per giro antiorario.
    * Il risultato è tra -PI -> PI
    */
    public static double angle2D(double y1, double x1, double y2, double x2){

        double dtheta,theta1,theta2;
        theta1 = Math.atan2(y1,x1);
        theta2 = Math.atan2(y2,x2);
        dtheta = theta2 - theta1;
        while (dtheta > Math.PI)
            dtheta -= TWOPI;
        while (dtheta < - Math.PI)
            dtheta += TWOPI;

        return(dtheta);
    }

    //Restituisce true se le coordinate sono valide, altrimenti false (inutilizzato, ma può tornare utile)
    public static boolean is_valid_gps_coordinate(double latitude, double longitude){
        if (latitude > -90 && latitude < 90 && longitude > -180 && longitude < 180){
            return true;
        }
        return false;
    }

    /* CHECK SIDE WITH DOT PRODUCT
       We assume that p1, p2, p3 are ordered in counterclockwise. Then we can check if p lies at left of the 3 oriented edges [p1, p2], [p2, p3] and [p3, p1].
       For that, first we need to consider  the 3 vectors v1, v2 and v3 that are respectively left-orthogonal to [p1, p2], [p2, p3] and [p3, p1] :
       v1 = <y2 - y1, -x2 + x1>
       v2 = <y3 - y2, -x3 + x2>
       v3 = <y1 - y3, -x1 + x3>
       Then we get the 3 following vectors :
       v1' = <x - x1, y - y1>
       v2' = <x - x2, y - y2>
       v3' = <x - x3, y - y3>
       At last, we compute the 3 dot products :
       dot1 = v1 . v1' = (y2 - y1)*(x - x1) + (-x2 + x1)*(y - y1)
       dot2 = v1 . v2' = (y3 - y2)*(x - x2) + (-x3 + x2)*(y - y2)
       dot3 = v3 . v3' = (y1 - y3)*(x - x3) + (-x1 + x3)*(y - y3)
       Finally, we can apply the interesting property :
       p lies in T if and only if 0 <= dot1 and 0 <= dot2 and 0 <= dot3
     */

    private static double side(Point p1, Point p2, Point px){
        return (((p2.getLongitude() - p1.getLongitude())*(px.getLatitude() - p1.getLatitude()))+((-p2.getLatitude() + p1.getLatitude())*(px.getLongitude() - p1.getLongitude())));
    }

    public static boolean pointInTriangle(Point myPos, Point rightVertex, Point leftVertex, Point px){
        boolean checkside1;
        if (side(myPos, rightVertex, px) >= 0) checkside1 = true; checkside1 = false;

        boolean checkside2;
        if (side(rightVertex, leftVertex, px) >= 0) checkside2 = true; checkside2 = false;

        boolean checkside3;
        if (side(leftVertex, myPos, px) >= 0) checkside3 = true; checkside3 = false;

        return (checkside1 && checkside2 && checkside3);
    }

}
