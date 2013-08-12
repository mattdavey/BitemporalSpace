# High Level Specification: Bitemportal Pricing And Risk Management Of Instruments

Be able to construct arbitrary instruments, and then amend those instruments based on a trade life cycle (time machine) concept. Instruments should be priced based on a market environment.  The market environment should be time machined - MTM from T-1, T be a continuum.  Full audit trail due to time machine of the instrument life cycle

The instrument life cycle should be based on the concept of events occuring on the trade.  In the case of an instrument having a forward date, and hence forward curves, it should be possible to move forwards in time or backwards to each instrument event, and re-price.  Instrument time line movement should be appropriately reflected in the market environment used to price the instrument.

The system should cater for an arbitrary set of instruments - exotic/complex and flow.

DAG graph should allow only the nodes of the instrument DAG to be recalculated, hence reducing CPU time.  
