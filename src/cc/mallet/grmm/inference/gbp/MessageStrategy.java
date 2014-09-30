/*
 * Copyright (c) 2014. Seonggyu Lee. All Rights Reserved.
 * User: Seonggyu Lee
 * Date: 14. 9. 30 오후 6:22
 * Created Date : $today.year.month.day
 * Last Modified : 14. 9. 30 오후 6:22
 * User email: shalomeir@gmail.com
 */
package cc.mallet.grmm.inference.gbp;

/**
 * Created: May 29, 2005
 *
 * @author <A HREF="mailto:casutton@cs.umass.edu>casutton@cs.umass.edu</A>
 * @version $Id: MessageStrategy.java,v 1.1 2007/10/22 21:37:58 mccallum Exp $
 */
public interface MessageStrategy {

  void sendMessage(RegionEdge edge);

  void setMessageArray(MessageArray oldMessages, MessageArray newMessages);
  MessageArray getOldMessages();
  MessageArray getNewMessages();

  MessageArray averageMessages(RegionGraph rg, MessageArray oldMessages, MessageArray newMessages, double weight);
}
