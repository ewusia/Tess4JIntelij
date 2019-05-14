package pl.ewa.tess4j.makieta;

import java.util.HashSet;

public class BuilderDlaPozycjiZakupow {

    private final HashSet<Long> lst;
    public BuilderDlaPozycjiZakupow()
    {
        lst=new HashSet<>();
    }
    public void clear()
    {
        lst.clear();
    }
    public void add(long idvalue)
    {
        lst.add(idvalue);
    }
    @Override public String toString()
    {
        final StringBuilder sb=new StringBuilder();
        for(Long idvalue:lst)
            sb.append(",").append(idvalue);
        return sb.toString().substring(1);
    }
}
