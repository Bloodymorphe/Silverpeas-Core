/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

/*
 * Week.java
 * this object represent the week in the monthCalendar viewGenerator
 * @see com.stratelia.webactiv.util.viewGenerator.html.monthCalendar
 * Created on 18 juin 2001, 10:26
 * @author Jean-Claude GROCCIA
 * jgroccia@silverpeas.com
 */

package com.stratelia.webactiv.util.viewGenerator.html.monthCalendar;

import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

import com.stratelia.silverpeas.silvertrace.SilverTrace;

/*
 * CVS Informations
 * 
 * $Id: Week.java,v 1.4 2006/03/21 12:09:52 neysseri Exp $
 * 
 * $Log: Week.java,v $
 * Revision 1.4  2006/03/21 12:09:52  neysseri
 * no message
 *
 * Revision 1.3  2005/04/14 18:35:44  neysseri
 * no message
 *
 * Revision 1.2  2004/06/24 17:16:38  neysseri
 * nettoyage eclipse
 *
 * Revision 1.1.1.1  2002/08/06 14:48:19  nchaix
 * no message
 *
 * Revision 1.5  2002/01/04 14:04:24  mmarengo
 * Stabilisation Lot 2
 * SilverTrace
 * Exception
 *
 */
 
/**
 * Class declaration
 *
 *
 * @author
 */
class Week extends Object
{

    private Date   startDate = null;
    private Date   endDate = null;
    private Date[] dateDayOfWeek = null;
    private Day[]  dayOfWeek = null;
    private Vector listEventWeek = null;
    private Vector listRow = null;


    /**
     * Creates the new Week
     * @param Day[], an array of Day
     * @param Vector, a list of object Event. the event of month
     * @see com.stratelia.webactiv.util.viewGenerator.html.monthCalendar.Day
     * @see java.util.Vector
     * @return object Week
     */
    public Week(Day[] day, Vector listEventMonth)
    {
        SilverTrace.info("viewgenerator", "Week.Constructor", "root.MSG_GEN_ENTER_METHOD");
        listEventWeek = new Vector();
        listRow = new Vector();

        this.dayOfWeek = day;
        int lg = day.length;

        // this.numbersDayOfWeek = numbersDayOfWeek;
        this.startDate = day[0].getDate();
        this.endDate = day[lg - 1].getDate();

        // to initialise the list event for this week with the list event for the current month
        listEventWeek = initListEventWeek(listEventMonth);

        this.listRow = initListRow(listEventWeek);
        SilverTrace.info("viewgenerator", "Week.Constructor", "root.MSG_GEN_EXIT_METHOD");
    }



    /**
     * *****************
     */

    /**
     * private method
     */

    /**
     * *****************
     */

    /**
     * to initialise the event of the week
     * @param Vector, the list of object Event. The event of current month
     * @return Vector, the list of objectEvent. The event of this week
     * @see com.stratelia.webactiv.util.viewGenerator.html.monthCalendar.Event
     * @see java.util.Vector
     */
    private Vector initListEventWeek(Vector listEventMonth)
    {
        Vector v = new Vector();

        if (listEventMonth.isEmpty())
        {
            return v;
        }
        Iterator itEvent = listEventMonth.iterator();

        while (itEvent.hasNext())
        {
            Event currentEvt = (Event) (itEvent.next());

            if (currentEvt.isInWeek(startDate, endDate))
            {
                Date stDateEvt = null;
                Date edDateEvt = null;

                // Date.comparTo(Date argument)=> the value 0 if the argument Date is equal to this Date;
                // a value less than 0 if this Date is before the Date argument;
                // and a value greater than 0 if this Date is after the Date argument

                // ram�ne le date du nouvel evenement (issue de l'evenement en cours) au limite de date de la semaine
                if (currentEvt.getStartDate().compareTo(startDate) <= 0)
                {
                    stDateEvt = startDate;
                }
                else
                {
                    stDateEvt = currentEvt.getStartDate();
                }

                if (currentEvt.getEndDate().compareTo(endDate) >= 0)
                {
                    edDateEvt = endDate;
                }
                else
                {
                    edDateEvt = currentEvt.getEndDate();
                }

                //Event evt = new Event(currentEvt.getId(), currentEvt.getName(), stDateEvt, edDateEvt, currentEvt.getUrl(), currentEvt.getPriority());
				Event evt = new Event(currentEvt);
				evt.setStartDate(stDateEvt);
				evt.setEndDate(edDateEvt);

                v.addElement(evt);
            }
        }
        SilverTrace.info("viewgenerator", "Week.initListEventWeek()", "root.MSG_GEN_EXIT_METHOD");
        return v;
    }

    /**
     * to initialise the object Row in this week.
     * if isn't possible to insert the event in the row, the new row is created
     * @param Vector, the list of object Event of current week
     * @return Vector, the list of object Row of current week
     * @see com.stratelia.webactiv.util.viewGenerator.html.monthCalendar.Event
     * @see com.stratelia.webactiv.util.viewGenerator.html.monthCalendar.Row
     * @see java.util.Vector
     */
    private Vector initListRow(Vector listEventWeek)
    {

        Vector listRow = new Vector();
        // il y a au moins une row dans une semaine
        Row    firstRow = new Row();

        listRow.addElement(firstRow);
        int index = 0;


        // contr�le s'il existe au moins un �v�nement dans la semaine
        if (listEventWeek.isEmpty())
        {
            return listRow;

        }
        Iterator it = listEventWeek.iterator();

        // tant qu'il y des �v�nements dans la semaine
        while (it.hasNext())
        {
            Event    evt = (Event) (it.next());

            boolean  resultAddEventInRow = false;

            Iterator itListRow = listRow.iterator();

            index = -1;

            while (!resultAddEventInRow)
            {
                // r�cup�ration du row courrant
                Row nextRow = (Row) itListRow.next();

                index++;
                // esaie l'ajout de l'event dans le row courrant
                resultAddEventInRow = addEventInRow(nextRow, evt);

                if (resultAddEventInRow)
                {
                    // l'�v�nement � �t� rajouter au row courrant, on remplace l'ancien row par le row courrant
                    // listRow.removeElementAt(index);
                    // listRow.add(index, tmpRow);
                    break;
                }
                else
                {
                    // il est impossible de rajouter l'evt dans le row courrant, on passe au row suivant s'il existe sinon on le cr�e
                    if (!(itListRow.hasNext()))
                    {
                        Row newRow = new Row();

                        resultAddEventInRow = addEventInRow(newRow, evt);  // tmp sera toujours != de null
                        // listRow.removeElementAt(index);
                        // ajout en dernier; index augmente de 1
                        index++;
                        listRow.addElement(newRow);
                        break;
                    }
                }
            }
        }
        return listRow;
    }



    /**
     * to add a object Event int the current row
     * @param Row, Event, the event who is add in the row
     * @return boolean, true if the operation "add" is succesfull, false else if
     * @see com.stratelia.webactiv.util.viewGenerator.html.monthCalendar.Event
     * @see com.stratelia.webactiv.util.viewGenerator.html.monthCalendar.Row
     * @see java.util.Vector
     */
    private boolean addEventInRow(Row currentRow, Event evt)
    {

        Vector listEvent = currentRow.getListEvent();

        if (listEvent.isEmpty())
        {
            currentRow.addEventIntRow(evt);
            return true;
        }

        Iterator itListEvent = listEvent.iterator();

        while (itListEvent.hasNext())
        {
            Event currentEvent = (Event) (itListEvent.next());

            if (!(evt.compareTo(currentEvent)))
            {
                return false;
            }
        }
        currentRow.addEventIntRow(evt);
        return true;
    }


    /**
     * **************************
     */

    /**
     * getter an setter methods
     */

    /**
     * **************************
     */
    public Date getStartDate()
    {
        return startDate;
    }

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public Date getEndDate()
    {
        return endDate;
    }

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public Vector getListEventWeek()
    {
        return listEventWeek;
    }

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public Date[] getDateDayOfWeek()
    {
        return dateDayOfWeek;
    }

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public Day[] getDayOfWeek()
    {
        return dayOfWeek;
    }

    /**
     * Method declaration
     *
     *
     * @param index
     *
     * @return
     *
     * @see
     */
    public Day getDayOfWeek(int index)
    {
        try
        {
            return dayOfWeek[index];
        }
        catch (java.lang.ArrayIndexOutOfBoundsException ae)
        {
            SilverTrace.warn("viewgenerator", "Week.getDayOfWeek()", "viewgenerator.EX_CANT_GET_DAY_OF_WEEK", "", ae);
            return dayOfWeek[0];
        }
    }

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public Vector getListRow()
    {
        return listRow;
    }
}