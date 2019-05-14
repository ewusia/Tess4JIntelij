package pl.ewa.tess4j.makieta;

import java.util.HashSet;

public class BuilderDlaCeny {

    private final HashSet<Float> zbior;
    public BuilderDlaCeny()
    {
        zbior=new HashSet<>();
    }
    public void clear()
    {
        zbior.clear();
    }
    public void add(float idvalue)
    {
        zbior.add(idvalue);
    }
    @Override public String toString()
    {
        if(zbior.size()<=0) return "null";
        final StringBuilder sb = new StringBuilder();
        for(Float idvalue:zbior)
            sb.append(",").append(idvalue);
        return sb.toString();
    }
}

