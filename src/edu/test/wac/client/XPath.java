package edu.test.wac.client;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.google.gwt.dom.client.Element;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

public class XPath {
 private List<Element> samplePath=new ArrayList<Element>();
private Element context;
 
 public XPath(Element e, Element context) {
	 this.context=context;
	 create(e);
 }

 public XPath(Element e) {
	 create(e);
 }

 private void create(Element e1) {
		if(e1==null || e1==context) {
			//s.insert(0, "/");
			return;
		}
		
		//
		samplePath.add(0, e1);
		create(e1.getParentElement());
	}
 
 final static private boolean useId=true;

 public String toString() {
  return toString(null);
 }
 
 public String toString(XPath generalizeXPath) {
	 return _toString(generalizeXPath, null);
 }
 
 public HashSet<String> getClassesNotInOther(XPath otherXPath) {
	 HashSet<String> dc=new HashSet<String>();
	 _toString(otherXPath, dc);
	 return dc;
 }
 
 public int getNrMissmatches(List<String> classes) {
	 List<String> _classes=getClasses(toString());
	 int mm=0;
	 
	 for(String c : classes) {
		 mm+=_classes.remove(c) ? 0 : 1;
	 }
	 
	 return mm+_classes.size();
 }
 
 public boolean containsClass(String clasz) {
	 	for(int i=0; i<samplePath.size(); i++) {
			Element e1=samplePath.get(i);
			//
			if(e1!=null) {
				
				if(e1.getNodeType()==Element.ELEMENT_NODE) {
					String className1=e1.getClassName();
					
					if(className1!=null && className1.trim().length()>0) {
						String cn[]=className1.split(" ");
						for(String c : cn) if(c.equals(clasz)) return true;
					}
					
				}
			}
		}
		
		return false;
 }
 
 private String _toString(XPath generalizeXPath, HashSet<String> diffClasses) {
		StringBuilder s=new StringBuilder();
		for(int i=0; i<samplePath.size(); i++) {
			Element e1=samplePath.get(i);
			//
			if(e1!=null) {
				StringBuilder es=new StringBuilder();
				StringBuilder selector=new StringBuilder();
				
				if(e1.getNodeType()==Element.ELEMENT_NODE) {
				es.append(e1.getTagName());
				
					if(useId) {
						String id=e1.getId();
						if(id!=null && id.trim().length()>0) {
					
							//
							if(selector.length()>0) selector.append(" and "); 
						    selector.append(" @id='"+id+"'");
							
						}
					}
					
					//
					String className1=e1.getClassName();
					ArrayList<String> cN1=new ArrayList<String>();
					ArrayList<String> cN2=new ArrayList<String>();
					ArrayList<String> cN=new ArrayList<String>();
					
					if(className1!=null && className1.trim().length()>0) {
						String cn[]=className1.split(" ");
						for(String c : cn) cN1.add(c);
					}
					
					//
					Element e2=generalizeXPath!=null ? generalizeXPath.get(i) : null;
					if(e2!=null) {
						if(e1.getNodeType()==e2.getNodeType() && 
								e1.getTagName().equals(e2.getTagName())) {
							
							
						String className2=e2.getClassName();
						if(className2!=null && className2.trim().length()>0) {
							String cn[]=className2.split(" ");
							for(String c : cn) cN2.add(c);
						}
						
						// find classes exclusive to xpath
						for(String c : cN1) {
							if(!cN2.contains(c)) {
								// classes not found from smaller
								if(diffClasses!=null) diffClasses.add(c);
							}
						}
						
						// sort so that cN1 is smaller than cN2
						if(cN2.size()<cN1.size()) {
							ArrayList<String> tmp=cN1;
							cN1=cN2;
							cN2=tmp;
						}
						
						for(String c : cN1) {
							if(cN2.contains(c)) {
								cN.add(c);
							}
						}
						
						} else {
							// after first missmatch we forget about generalizer, paths startedto diverge
							generalizeXPath=null;
						}
						
					} else {
						cN.addAll(cN1);
					}
					
										//
					StringBuilder cNs=new StringBuilder();
					
					for(String c : cN) {
							if(cNs.length()>0) cNs.append(" and ");
							cNs.append("contains(@class, '"+c+"')");
					}
					if(cNs.length()>0) {
						if(selector.length()>0) selector.append(" and "); 
						selector.append("("+cNs+")");
					}
				
					if(selector.length()>0) es.append("["+selector+"]");
				}
					else 
				if(e1.getNodeType()==Element.TEXT_NODE) {
					
					es.append("text()");
				}	
					
				// relative paths do not start with "/"
				if(context==null || s.length()>0) s.append("/");
				s.append(es);
			}
				
		}
			
		return s.toString();
		
	}
 
 private static RegExp r=RegExp.compile("contains\\(\\@class, '([^']*)'\\)", "g");
 
 public static List<String> getClasses(String xpath) {
	 
	List<String> cs=new ArrayList<String>(); 
	 
	MatchResult m=r.exec(xpath);
	while(m!=null) {
		String g=m.getGroup(1);
		if(g!=null) cs.add(g);
		//Window.alert(""+m.getGroupCount()+": "+g);
		m=r.exec(xpath);
	}
	
	return cs;
 }
 
 public Element get(int i) {
	 if(i>=samplePath.size()) return null;
	 return samplePath.get(i);
 }

 public static Element[] evaluateXPath(String xpath) {
	 return evaluateXPath(xpath, null);
	}
	
	public static native Element[] evaluateXPath(String xpath, Element context) /*-{
	if(context==null) context=$doc;

var xpathResult;

try {	
 xpathResult = $doc.evaluate(xpath, context, null, XPathResult.ORDERED_NODE_ITERATOR_TYPE, null);
} catch(e) {
alert( 'Error: Bad xpath: ' + e );
	return [];
}

var x=[];
try {
	  var thisNode = xpathResult.iterateNext();
	  
	  while (thisNode) {
	   x[x.length]= thisNode ;
	    thisNode = xpathResult.iterateNext();
	  }           
	}
	catch (e) {
	  alert( 'Error: Document tree modified during iteration ' + e );
	  return x;
	}
	
	return x;
}-*/;
}
