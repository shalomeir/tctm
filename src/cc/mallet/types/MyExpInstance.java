/*
 * Copyright (c) 2014. Seonggyu Lee. All Rights Reserved.
 * User: Seonggyu Lee
 * Date: 14. 9. 30 오후 6:22
 * Created Date : $today.year.month.day
 * Last Modified : 14. 9. 30 오후 6:22
 * User email: shalomeir@gmail.com
 */




package cc.mallet.types;

import cc.mallet.util.CommandOption;
import cc.mallet.util.MalletLogger;
import cc.mallet.util.PropertyList;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.logging.Logger;

/**
	 A machine learning "example" to be used in training, testing or
	 performance of various machine learning algorithms.

	 <p>An instance contains four generic fields of predefined name:
     "data", "target", "name", and "source".   "Data" holds the data represented
    `by the instance, "target" is often a label associated with the instance,
     "name" is a short identifying name for the instance (such as a filename),
     and "source" is human-readable sourceinformation, (such as the original text).

     <p> Each field has no predefined type, and may change type as the instance
     is processed. For example, the data field may start off being a string that
     represents a file name and then be processed by a {@link cc.mallet.pipe.Pipe} into a CharSequence
     representing the contents of the file, and eventually to a feature vector
     holding indices into an {@link cc.mallet.types.Alphabet} holding words found in the file.
     It is up to each pipe which fields in the Instance it modifies; the most common
     case is that the pipe modifies the data field.

	 <p>Generally speaking, there are two modes of operation for
	 Instances.  (1) An instance gets created and passed through a
	 Pipe, and the resulting data/target/name/source fields are used.
	 This is generally done for training instances.  (2) An instance
	 gets created with raw values in its slots, then different users
	 of the instance call newPipedCopy() with their respective
	 different pipes.  This might be done for test instances at
	 "performance" time.

	 <p> Rather than store an {@link cc.mallet.types.Alphabet} in the Instance,
	 we obtain it through the Pipe instance variable, because the Pipe also
	 indicates where the data came from and how to interpret the Alphabet.

     <p>Instances can be made immutable if locked.
	 Although unlocked Instances are mutable, typically the only code that
	 changes the values in the four slots is inside Pipes.

     <p> Note that constructing an instance with a pipe argument means
     "Construct the instance and then run it through the pipe".
     {@link cc.mallet.types.InstanceList} uses this method
     when adding instances through a pipeInputIterator.

   @see cc.mallet.pipe.Pipe
   @see Alphabet
   @see InstanceList

   @author Andrew McCallum <a href="mailto:mccallum@cs.umass.edu">mccallum@cs.umass.edu</a>
 */

public class MyExpInstance extends Instance implements Serializable, AlphabetCarrying, Cloneable
{
	private static Logger logger = MalletLogger.getLogger(MyExpInstance.class.getName());

    protected Object[] dataArray;				// this is used for another tagged data - my exp


    /** for sg
     */
    public MyExpInstance(Instance instance)
    {
        super(instance.getData(),instance.getTarget(),instance.getName(),instance.getSource());
        this.dataArray = null;
    }


	/** In certain unusual circumstances, you might want to create an Instance
	 * without sending it through a pipe.
	 */
	public MyExpInstance(Object data, Object target, Object name, Object source, Object[] dataArray)
	{
	    super(data,target,name,source);
        this.dataArray = dataArray;
	}

    public Object[] getDataArray() {
        return dataArray;
    }

    public Alphabet getDataArrayAlphabet(int i) {
		if (dataArray[i] instanceof AlphabetCarrying)
			return ((AlphabetCarrying)dataArray[i]).getAlphabet();
		else
			return null;
	}


	public Alphabet[] getFullAlphabets()
	{
		return new Alphabet[] {getDataAlphabet(), getTargetAlphabet(), getDataArrayAlphabet(0),getDataArrayAlphabet(1),getDataArrayAlphabet(2),getDataArrayAlphabet(3)};
	}
	
	public boolean alphabetsFullMatch (MyExpInstance object)
	{
		Alphabet[] oas = object.getFullAlphabets();
		return (oas.length == 6) && oas[0].equals(getDataAlphabet()) && oas[1].equals(getDataAlphabet()) && oas[2].equals(getDataArrayAlphabet(0)) && oas[3].equals(getDataArrayAlphabet(1)) && oas[4].equals(getDataArrayAlphabet(2)) && oas[5].equals(getDataArrayAlphabet(3));
	}


	public void setDataArray (Object[] dta) {
		// This test isn't strictly necessary, but might catch some typos.
		assert (dataArray == null || dataArray instanceof Object[]);
		if (!locked) dataArray = dta;
		else throw new IllegalStateException ("Instance is locked.");
	}


	public MyExpInstance shallowCopy ()
	{
		MyExpInstance ret = new MyExpInstance(data, target, name, source, dataArray);
		ret.locked = locked;
		ret.properties = properties;
		return ret;
	}
	
	public Object clone ()
	{
		return shallowCopy();
	}


	// Serialization of Instance

	private static final long serialVersionUID = 1;
	private static final int CURRENT_SERIAL_VERSION = 0;
	
	private void writeObject (ObjectOutputStream out) throws IOException {
		out.writeInt (CURRENT_SERIAL_VERSION);
		out.writeObject(data);
		out.writeObject(target);
		out.writeObject(name);
		out.writeObject(source);
        out.writeObject(dataArray);

        out.writeObject(properties);
		out.writeBoolean(locked);
	}
	
	private void readObject (ObjectInputStream in) throws IOException, ClassNotFoundException {
		int version = in.readInt ();
		data = in.readObject();
		target = in.readObject();
		name = in.readObject();
		source = in.readObject();
        dataArray = (Object[]) in.readObject();

        properties = (PropertyList) in.readObject();
		locked = in.readBoolean();
	}

}
