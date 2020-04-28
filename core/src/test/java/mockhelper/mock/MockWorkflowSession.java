package mockhelper.mock;

import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowService;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.collection.util.ResultSet;
import com.day.cq.workflow.exec.*;
import com.day.cq.workflow.exec.filter.WorkItemFilter;
import com.day.cq.workflow.model.WorkflowModel;
import com.day.cq.workflow.model.WorkflowModelFilter;
import org.apache.jackrabbit.api.security.user.Authorizable;

import javax.jcr.Session;
import javax.jcr.version.VersionException;
import java.security.AccessControlException;
import java.util.Dictionary;
import java.util.List;
import java.util.Map;

public class MockWorkflowSession implements WorkflowSession {
    @Override
    public WorkflowService getWorkflowService() {
        return null;
    }

    @Override
    public Session getSession() {
        return null;
    }

    @Override
    public Authorizable getUser() {
        return null;
    }

    @Override
    public boolean evaluate(WorkflowData workflowData, String s) {
        return false;
    }

    @Override
    public void deployModel(WorkflowModel workflowModel) throws WorkflowException {

    }

    @Override
    public WorkflowModel createNewModel(String s) throws WorkflowException {
        return null;
    }

    @Override
    public WorkflowModel createNewModel(String s, String s1) throws WorkflowException {
        return null;
    }

    @Override
    public void deleteModel(String s) throws WorkflowException {

    }

    @Override
    public WorkflowModel[] getModels() throws WorkflowException {
        return new WorkflowModel[0];
    }

    @Override
    public WorkflowModel[] getModels(WorkflowModelFilter workflowModelFilter) throws WorkflowException {
        return new WorkflowModel[0];
    }

    @Override
    public ResultSet<WorkflowModel> getModels(long l, long l1) throws WorkflowException {
        return null;
    }

    @Override
    public ResultSet<WorkflowModel> getModels(long l, long l1, WorkflowModelFilter workflowModelFilter) throws WorkflowException {
        return null;
    }

    @Override
    public WorkflowModel getModel(String s) throws WorkflowException {
        return null;
    }

    @Override
    public WorkflowModel getModel(String s, String s1) throws WorkflowException, VersionException {
        return null;
    }

    @Override
    public Workflow startWorkflow(WorkflowModel workflowModel, WorkflowData workflowData) throws WorkflowException {
        return null;
    }

    @Override
    public Workflow startWorkflow(WorkflowModel workflowModel, WorkflowData workflowData, Dictionary<String, String> dictionary) throws WorkflowException {
        return null;
    }

    @Override
    public Workflow startWorkflow(WorkflowModel workflowModel, WorkflowData workflowData, Map<String, Object> map) throws WorkflowException {
        return null;
    }

    @Override
    public void terminateWorkflow(Workflow workflow) throws WorkflowException {

    }

    @Override
    public void resumeWorkflow(Workflow workflow) throws WorkflowException {

    }

    @Override
    public void suspendWorkflow(Workflow workflow) throws WorkflowException {

    }

    @Override
    public WorkItem[] getActiveWorkItems() throws WorkflowException {
        return new WorkItem[0];
    }

    @Override
    public ResultSet<WorkItem> getActiveWorkItems(long l, long l1) throws WorkflowException {
        return null;
    }

    @Override
    public ResultSet<WorkItem> getActiveWorkItems(long l, long l1, WorkItemFilter workItemFilter) throws WorkflowException {
        return null;
    }

    @Override
    public WorkItem[] getAllWorkItems() throws WorkflowException {
        return new WorkItem[0];
    }

    @Override
    public ResultSet<WorkItem> getAllWorkItems(long l, long l1) throws WorkflowException {
        return null;
    }

    @Override
    public WorkItem getWorkItem(String s) throws WorkflowException {
        return null;
    }

    @Override
    public Workflow[] getWorkflows(String[] strings) throws WorkflowException {
        return new Workflow[0];
    }

    @Override
    public ResultSet<Workflow> getWorkflows(String[] strings, long l, long l1) throws WorkflowException {
        return null;
    }

    @Override
    public Workflow[] getAllWorkflows() throws WorkflowException {
        return new Workflow[0];
    }

    @Override
    public Workflow getWorkflow(String s) throws WorkflowException {
        return null;
    }

    @Override
    public void complete(WorkItem workItem, Route route) throws WorkflowException {

    }

    @Override
    public List<Route> getRoutes(WorkItem workItem) throws WorkflowException {
        return null;
    }

    @Override
    public List<Route> getRoutes(WorkItem workItem, boolean b) throws WorkflowException {
        return null;
    }

    @Override
    public List<Route> getBackRoutes(WorkItem workItem) throws WorkflowException {
        return null;
    }

    @Override
    public List<Route> getBackRoutes(WorkItem workItem, boolean b) throws WorkflowException {
        return null;
    }

    @Override
    public WorkflowData newWorkflowData(String s, Object o) {
        return null;
    }

    @Override
    public List<Authorizable> getDelegatees(WorkItem workItem) throws WorkflowException {
        return null;
    }

    @Override
    public void delegateWorkItem(WorkItem workItem, Authorizable authorizable) throws WorkflowException, AccessControlException {

    }

    @Override
    public List<HistoryItem> getHistory(Workflow workflow) throws WorkflowException {
        return null;
    }

    @Override
    public void updateWorkflowData(Workflow workflow, WorkflowData workflowData) {

    }

    @Override
    public void logout() {

    }

    @Override
    public boolean isSuperuser() {
        return false;
    }

    @Override
    public void restartWorkflow(Workflow workflow) throws WorkflowException {

    }
}
