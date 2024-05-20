public class Vertex {
    private long id;
    private double longitude;
    private double latitude;

    public Vertex(long id, double longitude, double latitude) {
        this.id = id;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    @Override
    public String toString() {
        return "Vertex " + id + ": (" + latitude + ", " + longitude + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Vertex)) { return false; }
        else if ( this.id != ((Vertex) obj).id ) { return false; }
        else return true;
    }
}
