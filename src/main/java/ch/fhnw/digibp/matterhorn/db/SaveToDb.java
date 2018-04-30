package ch.fhnw.digibp.matterhorn.db;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import javax.inject.Named;

@Named
public class SaveToDb implements JavaDelegate {

    public void execute(DelegateExecution execution){
        execution.setVariable("delegateExecuteData", "delegate execute result: " + execution.getVariable("sampleData"));
    }
}
