package org.wei86609.osmanthus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.wei86609.osmanthus.event.Event;
import org.wei86609.osmanthus.node.Node;
import org.wei86609.osmanthus.node.Node.TYPE;
import org.wei86609.osmanthus.node.executor.NodeExecutor;

public class FlowEngine{

    private final static Logger logger = Logger.getLogger(FlowEngine.class);

    private Map<TYPE,NodeExecutor> nodeExecutorMap;

    public void addNodeExecutor(List<NodeExecutor> nodeExecutors){
        if(nodeExecutorMap==null){
            nodeExecutorMap=new HashMap<TYPE,NodeExecutor>();
        }
        for(NodeExecutor nodeExecutor:nodeExecutors){
            nodeExecutorMap.put(nodeExecutor.getType(), nodeExecutor);
        }
    }

    public void addNodeExecutor(NodeExecutor nodeExecutor){
        if(nodeExecutorMap==null){
            nodeExecutorMap=new HashMap<TYPE,NodeExecutor>();
        }
        nodeExecutorMap.put(nodeExecutor.getType(), nodeExecutor);
    }

    public Boolean execute(Event event,String nodeId) throws Exception {
        logger.debug("Osmanthus start to execute the event["+event+"]");
        Node firstNode=ConfigurationBuilder.getBuilder().loadOneFlow(event.getEventId());
        if(StringUtils.isBlank(nodeId)){
            nodeId=firstNode.getId();
            logger.debug("Node is blank, will get the first node ["+nodeId+"] of flow to execute.");
        }
        runFlowNode(event,nodeId);
        logger.debug("Osmanthus execute the event {"+event+"} end");
        return true;
    }

    private void runFlowNode(Event event,String nodeId)throws Exception{
        if(StringUtils.isBlank(nodeId)){
            return;
        }
        Node node=ConfigurationBuilder.getBuilder().getAvaiableNodes().get(nodeId);
        if(node==null){
            return;
        }
        String nextNodeId= nodeExecutorMap.get(node.getType()).execute(event, node);
        runFlowNode(event,nextNodeId);
    }
    
    public void runRule(Event event,String ruleId)throws Exception{
        if(StringUtils.isBlank(ruleId)){
            return;
        }
        Node node=ConfigurationBuilder.getBuilder().loadConfiguration().getAvaiableNodes().get(ruleId);
        if(node==null){
            return;
        }
        nodeExecutorMap.get(node.getType()).execute(event, node);
    }

}
