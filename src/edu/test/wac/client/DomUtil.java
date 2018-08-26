package edu.test.wac.client;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Text;

public class DomUtil {
	
	static public interface NodeHandler {

        public boolean returnRejectedNode();

        /**
         *
         * @param text current node
         * @return The meaning of the returned value depends on using method
         */
        public boolean onText(Text text);

        /**
         *
         * @param element Current element
         * @return The meaning of the returned value depends on using method
         */
        public boolean onElement(Element element);
    }
	
    /**
     * This is a bit redundant because getParentElement (in contrast to getParentNode) should not return the document as parent of body.
     * This method is used to make the code more readable and assert hat th parent of body is null!
     * @param node Child
     * @return Parent elemen (null if no parent or child is body)
     */
    static public Element getParentInBody(Node node) {
        if(node==node.getOwnerDocument().getBody()) return null;
        return node.getParentElement();
    }

	/**
     * Return false from handler.onText() or handler.onElement() to stop the iteration.
     * Return true from handler.returnRejectedNode() to return the rejected node instead of the last accepted node.
     * @param node Start node
     * @param handler Handler to stop iteration.
     * @param includeStartNode Set true to pass the start node to the handler too.
     * @return Returns the last accepted or the rejected (depending on handler) or null if no more parent was found.
     */
    public static Node walkUp(Node node, NodeHandler handler, boolean includeStartNode) {
        Node lastAcceptedNode=null;
        do {
            //
            if(includeStartNode) {
                if (node.getNodeType() == Node.TEXT_NODE) {
                    //noinspection ConstantConditions
                    if (!handler.onText((Text) node)) break;
                } else {
                    if (!handler.onElement((Element) node)) break;
                }

                lastAcceptedNode=node;
            }
            includeStartNode=true;
        } while ((node = DomUtil.getParentInBody(node))!=null);

        return handler.returnRejectedNode() ? node : lastAcceptedNode;
    }
    
    static public interface CommonAncestorHandler {
        /**
         *
         * @param n Current node which will be used in ancestor comparison if it is an element.
         * @return Return false to stop excluding this and any more nodes in the parent comparison.
         */
        public boolean onNodeA(Node n);

        /**
         *
         * @param n Current node which will be used in ancestor comparison if it is an element.
         * @return Return false to stop excluding this and any more nodes in the parent comparison.
         */
        public boolean onNodeB(Node n);
    }


    static public Element findCommonAncestor(Node a, Node b) {
        return findCommonAncestor(a, b, null);
    }

    /**
     * Return false from handler.onNodeA(n) or handler.onNodeB(n) to stop iteration before this node's parent.
     * @param a Start node a (typically left side)
     * @param b Start node b (typically right side)
     * @param handler Handler to stop iteration.
     * @return Return the common ancestor or null if none found.
     */
    static public Element findCommonAncestor(Node a, Node b, final CommonAncestorHandler handler) {

        final List<Element> aPs = new ArrayList<Element>();
        walkUp(a, new NodeHandler() {
            public boolean returnRejectedNode() {
                return false;
            }

            public boolean onText(Text text) {
                return onNode(text);
            }

            public boolean onElement(Element element) {
                return onNode(element);
            }

            private boolean onNode(Node n) {
                if (handler != null && !handler.onNodeA(n)) return false;
                if (n.getNodeType() == Node.ELEMENT_NODE) aPs.add((Element) n);
                return true;
            }

        }, true);

        //
        final Set<Element> bPs = new HashSet<Element>();

        walkUp(b, new NodeHandler() {
            public boolean returnRejectedNode() {
                return false;
            }

            public boolean onText(Text text) {
                return onNode(text);
            }

            public boolean onElement(Element element) {
                return onNode(element);
            }

            private boolean onNode(Node n) {
                if (handler != null && !handler.onNodeB(n)) return false;
                if (n.getNodeType() == Node.ELEMENT_NODE) bPs.add((Element) n);
                return true;
            }

        }, true);

        //
        for (Element aP : aPs) {

            if(bPs.contains(aP)) return aP;
        }

        return null;
    }

}
