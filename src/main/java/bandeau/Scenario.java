package bandeau;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.LinkedList;

/**
 * Classe utilitaire pour représenter la classe-association UML
 */
class ScenarioElement {

    Effect effect;
    int repeats;

    ScenarioElement(Effect e, int r) {
        effect = e;
        repeats = r;
    }
}
/**
 * Un scenario mémorise une liste d'effets, et le nombre de repetitions pour chaque effet
 * Un scenario sait se jouer sur un bandeau.
 */
public class Scenario {

    private final List<ScenarioElement> myElements = new LinkedList<>();
    private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    private final Lock r = rwl.readLock();
    private final Lock w = rwl.writeLock();
    /**
     * Ajouter un effect au scenario.
     *
     * @param e l'effet à ajouter
     * @param repeats le nombre de répétitions pour cet effet
     */
    public void addEffect(Effect e, int repeats) {
        w.lock(); try{
            myElements.add(new ScenarioElement(e, repeats));
        }finally{w.unlock();}
    }

    /**
     * Jouer ce scenario sur un bandeau
     *
     * @param b le bandeau ou s'afficher.
     */
    public void playOn(ThreadSafeBandeau b){
        r.lock(); try {
            //On crée un thread
            Thread t = new Thread(
                    () -> {
                        //On bloque le bandeau au lancement du scénario
                        b.verrouille();
                        for (ScenarioElement element : myElements) {
                            for (int repeats = 0; repeats < element.repeats; repeats++) {
                                element.effect.playOn(b);
                            }
                        }
                        //Apres avoir fini le scenario on deverrouille le bandeau
                        b.deverrouille();
                    }
            );
            t.start();
        } finally { r.unlock(); }

    }
}