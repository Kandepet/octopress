package com.lar5;

import com.lar5.Caesar;
import com.lar5.MoveSequence;

import java.util.Iterator;
import java.util.Vector;
import java.text.DecimalFormat;

/**
 * The AnimationThread handles the animation, so the main thread can listen for input.
 *
 * It also takes care of storing and un/redoing user moves.
 *
 * About the threading model:
 * We have at most 3 threads, one which is the applet and handles user input, an other
 * that handles animation, and in the case of solving mode, a thread to update the timer every second..
 *
 * The only synchronization happening now is that drawing the cube and changing the data the drawing is based
 * on are not colliding. There could be more, but I don't think the lack of it has any serious consequences.
 */
final class AnimationThread extends Thread
{
    MoveSequence sequence;
    Caesar jc;
    int action = 0;
    SingleMove latestQueuedMove;

    // Defined Actions
    public final static int PLAY = 1;
    public final static int REW_PLAY = 2;
    public final static int STEP = 3;
    public final static int REW_STEP = 4;
    public final static int PAUSE = 5;
    public final static int FIRST = 6;
    public final static int LAST = 7;

    public final static int USER_MOVE = 8;
    public final static int MIX_FOR_SOLVE = 9;
    public final static int BT_MIX_DONE = 10;

    boolean threadCreatedAndReady = false; // avoid race condition at startup


    // Solution timer data goes here for now. May be refactored later.
    long mixedComplete;  // Timestamp of start of inspection phase
    boolean inspectionPhase = false;  // true if we're waiting for a solve to start
    TimerThread solveTimer;
    String startPosition; // remember to print out later
    Vector userMoves;
    int userMovesCursor; // userMovesCursor == 5, refers to userMoves[4], which is element number 5

    public AnimationThread(MoveSequence sequence, Caesar mainApp)
    {
        super("PLAY"); // name of thread
        this.sequence = sequence;
        this.jc = mainApp;

        userMoves = new Vector(50); // Trying to be JDK 1.1 compatible
    }

    public void clearUserMoves()
    {
        userMoves.clear();
        userMovesCursor = 0;
    }

    /**
     * The seconds timer for solving
     */
    private TimerThread getSolveTimer()
    {
        if (solveTimer == null) {
            solveTimer = new TimerThread(jc);
            solveTimer.start(); // creates thread object
        }
        return solveTimer;
    }

    // This is called by the MAIN thread, to communicate with the PLAY thread. Which is this thread. Yeah, it's supposed to be this confusing...
    /**
     * Queues up a new action, In the sense that we wait until any currently performed action is completed until we
     * signal this new action. Let the Java event handling handle any actual event queueing.
     */
    public void queueAction(int actionCode)
    {
        // if a previously scheduled action hasn't been consumed yet, we simply wait
        // until it has been. The previous action *has* been interrupted, and we're
        // just waiting for it to finish up, so it should happen quickly
        //
        // Note that the situation here is that one action is running, an other has been queued up and this is the *third* one coming in.
        while (action != 0) {
            try {
                Thread.sleep(100); // 0.1 sec
            } catch (InterruptedException e) {
                // we shouldn't normally end up here, but do nothing anyway
            }
        }
        action = actionCode;
        this.interrupt(); // Interupts the AnimationThread, but is called from the main thread.
    }

    /**
     * Used only for user initiated events, since they happen outside our control
     *
     * @param side  this is the raw side number
     */
    public void queueSpinMove(int side, int slices, boolean forward, int num, boolean animate, double spintime, boolean record) {
        latestQueuedMove = new SingleMove(side, slices, forward, num, animate, spintime, jc);
        if (latestQueuedMove.isReal()) {
            sequence.userTwisted();

            if (record) {
                while (userMoves.size() > userMovesCursor) {
                    userMoves.removeElementAt(userMovesCursor);
                }
                userMoves.add(latestQueuedMove);
                userMovesCursor++;
            }
        }
        queueAction(USER_MOVE);
    }

    public void undoUserMove()
    {
        if (userMovesCursor > 0)
        {
            SingleMove reversee = (SingleMove) userMoves.elementAt(userMovesCursor-1) ;
            queueSpinMove(reversee.rawSide(), reversee.slices, !reversee.forward,  reversee.num, reversee.animate, reversee.spintime, false);
            userMovesCursor--;
        }
    }

    public void redoUserMove()
    {
        if (userMovesCursor < userMoves.size()) {
            SingleMove redoee = (SingleMove) userMoves.elementAt(userMovesCursor);
            queueSpinMove(redoee.rawSide(), redoee.slices, redoee.forward, redoee.num, redoee.animate, redoee.spintime, false);
            userMovesCursor++;
        }
    }

    public void rewindUserMoves()
    {
        while (userMovesCursor > 0) {
            SingleMove rewindee = (SingleMove) userMoves.elementAt(userMovesCursor-1) ;
            rewindee.unExecute();
            userMovesCursor--;
        }
    }

    /**
     * Turns "F R R2 L L'" into "F R'"
     */
    void optimizeUserMoves()
    {
        boolean changed;
        do {
            changed = false;
            for (int u=1; u<userMoves.size(); u++) {
                if (userMovesCursor != u) { // can't merge next and prev move!
                    SingleMove prev = (SingleMove) userMoves.elementAt(u-1);
                    SingleMove move = (SingleMove) userMoves.elementAt(u);

                    if (move.logicalSide == prev.logicalSide) {
                        changed = true;
                        int num1 = move.forward ? move.num : 4 - move.num;
                        int num2 = prev.forward ? prev.num : 4 - prev.num;
                        int sum = (num1 + num2) % 4;
                        if (sum == 0) { // cancel
                            userMoves.removeElementAt(u);
                            if (userMovesCursor > u)
                                userMovesCursor--;
                        } else {
                            move.forward = (sum < 3);
                            if (move.forward)
                                move.num = sum;
                            else
                                move.num = 1;
                        }
                        userMoves.removeElementAt(u-1);
                        if (userMovesCursor > u-1)
                            userMovesCursor--;
                    }
                }
            }
        } while (changed);
    }

    /**
     * The user moves as a "F R2 D'" type string
     * @return
     */
    String userMoveText()
    {
        StringBuffer buf = new StringBuffer();
        for (int u=0; u<userMoves.size(); u++) {
            buf.append(userMoves.elementAt(u)).append(" ");
            if (u == userMovesCursor-1)
                buf.append("¥ ");
        }
        if (userMovesCursor < userMoves.size())
            buf.append(userMovesCursor + "/");
        buf.append(userMoves.size());
        return buf.toString();
    }

    public void showUserMoves()
    {
        jc.view.setText(userMoveText());
    }

    /**
     * This is the main loop for the animation thread
     */
    public void run()
    {
        while (true) {
            if (action == 0) {
                try {
                    // The PLAY thread will sleep here until a user event produces an interruption.
                    threadCreatedAndReady = true;
                    sleep(500000); // 500 seconds
                  } catch(InterruptedException e) {
                    // Do nothing, this is normal
                  }
            }
            int actodo = action;
            action = 0;   // Makes the actions interuptable

            switch (actodo) {
                case PLAY:
                    jc.getMoveSeq().play();
                    break;
                case REW_PLAY:
                    jc.getMoveSeq().rewind();
                    break;
                case STEP:
                    jc.getMoveSeq().doStep();
                    break;
                case REW_STEP:
                    jc.getMoveSeq().rewStep();
                    break;
                case PAUSE:
                    jc.getMoveSeq().pause();
                    break;
                case FIRST:
                    jc.getMoveSeq().goFirst();
                    break;
                case LAST:
                    jc.getMoveSeq().goLast();
                    break;
                case USER_MOVE:
                    // Perform move
                    latestQueuedMove.execute();

                    // Record move
                    switch (jc.state) {
                        case Caesar.REGULAR_MODE:
                            jc.view.refresh();
                            break;
                        case Caesar.SOLVE_MODE :
                            if (inspectionPhase && latestQueuedMove.isReal()) {
                                getSolveTimer().startTiming(latestQueuedMove.timestamp);
                                inspectionPhase = false;
                            }
                            if (jc.cube.isSolved()) {
                                solveTimer.stopTiming();
                                long timeSpent = ((SingleMove) userMoves.lastElement()).timestamp - ((SingleMove) userMoves.firstElement()).timestamp;
                                jc.view.setText(twoDecimals(0.001*timeSpent) + " sec     " + userMoves.size() + " moves");
                                printSolutionData();
                            }
                            break;
                        case Caesar.BACKTRACK_MODE:
                            optimizeUserMoves();
                            if (jc.btInProgress()) { // let user keep playing after failing
                                if (jc.cube.isSolved()) {
                                    jc.btAttemptFinished(true);
                                    jc.view.setText("You succeeded. Solved " + jc.btSolves + " out of " + jc.btTries);
                                } else if (userMoves.size() >= jc.btMoves) {
                                    jc.btAttemptFinished(false);
                                    jc.view.setText("You failed. Solved " + jc.btSolves + " out of " + jc.btTries);
                                }
                            }

                            break;
                    }
                    break;
                case MIX_FOR_SOLVE:
                    getSolveTimer().stopTiming();
                    clearUserMoves();
                    jc.getMoveSeq().play();
                    solveMixComplete();
                    break;
                case BT_MIX_DONE:
                    btMixComplete();
                    break;
            }
            jc.repaint();  // to update buttons
        }
    }

    public void solveMixComplete()
    {
        mixedComplete = System.currentTimeMillis();
        startPosition = jc.cube.positionString();
        clearUserMoves();
        jc.state = Caesar.SOLVE_MODE;
        inspectionPhase = true;
        jc.view.setText(" Inspection time");
//        System.out.println("------- Cube mixed ---------"); //@@@
    }

    public void btMixComplete()
    {
        clearUserMoves();
        jc.state = Caesar.BACKTRACK_MODE;
//        System.out.println("------- Cube backtracked ---------"); //@@@
    }

    String getTimerSeconds()
    {
        return getSolveTimer().sec;
    }

    public boolean isReady()
    {
        return threadCreatedAndReady;
    }

    /**
     * Prints the results of a userMoves attempt.
     */
    private void printSolutionData()
    {
        System.out.println("------- Cube solved ---------");
        int usedMoves = userMoves.size();
        long firstMoveTime = ((SingleMove) userMoves.get(0)).timestamp;
        String solutionTime = twoDecimals(0.001*(((SingleMove) userMoves.get(usedMoves-1)).timestamp - firstMoveTime));
        System.out.println("Solution time: " + solutionTime + ",  Used moves: " + usedMoves + ", inspection time: " + twoDecimals(0.001*(firstMoveTime - mixedComplete)));
        System.out.println("start position: " + startPosition);

        System.out.print("Move: ");
        for (Iterator iterator = userMoves.iterator(); iterator.hasNext();) {
            SingleMove move = (SingleMove) iterator.next();
            System.out.print(move.toString());
        }
        System.out.println();

        for (Iterator iterator = userMoves.iterator(); iterator.hasNext();) {
            SingleMove move = (SingleMove) iterator.next();
            System.out.println(move.toString() + "  --  " + twoDecimals(0.001*(move.timestamp - firstMoveTime)));
        }
    }

    DecimalFormat twoDecimalFormatter;

    String twoDecimals(double number)
    {
        if (twoDecimalFormatter == null)
            twoDecimalFormatter = new DecimalFormat("###.00");
        return twoDecimalFormatter.format(number);
    }


    // ##############################################################################

    /**
     * Redraws every second so the timer can be seen even if the user isn't turning.
     */
    final class TimerThread extends Thread
    {
        Caesar jc;
        private long startTime;
        private boolean timing = false;
        String sec = "";

        public TimerThread(Caesar applet)
        {
            jc = applet;
        }

        public void startTiming(long _startTime)
        {
            startTime = _startTime;
            sec = "0";
            timing = true;
//            System.out.println("XXxXX"); //@@@
            interrupt();
        }

        public void stopTiming()
        {
            sec = "";
            timing = false;
        }

        public void run()
        {
            while (true) {
                try {
                    sleep(31500000); // 1 Year
                } catch (InterruptedException e) {  }

                while (timing) {
                    long now = System.currentTimeMillis();
                    sec = "" + (now - startTime + 100)/1000; // "+ 100" to avoid 21999 ms displayed as 21 sec
                    jc.repaint();
                    
                    try {
                        sleep(1000 - ((System.currentTimeMillis() - startTime) % 1000)); // Sleep until next whole second
                    } catch (InterruptedException e) {
                    }
                }
            }
        }
    }

}
