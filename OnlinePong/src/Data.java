public class Data extends Thread implements java.io.Serializable
{
    public int Pxpos, Pypos, Bxpos, Bypos;

    public Data(int x, int y, int z, int q)
    {
        Pxpos= x;
        Pypos= y;
        Bxpos= z;
        Bypos= q;
    }
}