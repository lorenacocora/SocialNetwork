package socialnetwork.utils;

import java.util.ArrayList;
import java.util.List;

public class MyObservable {

    private List<MyObserver> observers=new ArrayList<>();

    public void addObserver(MyObserver obs){
        observers.add(obs);
    }

    public void notifyObservers(){
        for(MyObserver obs : observers)
            obs.update();
    }
}
