(ns org.wol.minotaur.sequencer
  (:import [javax.sound.midi MidiSystem Sequence Receiver MidiEvent ShortMessage Sequencer
            Sequencer$SyncMode ControllerEventListener]))

;; (def *pattern* (agent {1 [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0]
;;                         2 [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0]
;;                         3 [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0]
;;                         4 [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0]}))

(def *pattern* (atom {0 [false false false false] 1 [false false false false] 2 [false false false false] 3 [false false false false]
                      4 [false false false false] 5 [false false false false] 6 [false false false false] 7 [false false false false]
                      8 [false false false false] 9 [false false false false] 10  [false false false false] 11 [false false false false]
                      12 [false false false false] 13 [false false false false] 14 [false false false false] 15 [false false false false]}))

(def *sequencer* (MidiSystem/getSequencer))
(def *sequence* (Sequence. Sequence/PPQ 960))
(def *track* (.createTrack *sequence*))
(def *midi-receive-spp-fn* (atom nil))

(defmulti midi-receive (fn [message timestamp] (.getStatus message)))
(defmethod midi-receive ShortMessage/SONG_POSITION_POINTER [message timestamp]
  (@*midi-receive-spp-fn* (.getData2 message)))

(defmethod midi-receive :default [message timestamp]
  (comment (println "status byte:" (.getStatus message))))

(def midi-receive-handler (proxy [Receiver] []
                             (send [message timestamp]
                                   (midi-receive message timestamp)
                                   )))

(def *note-on* (doto (ShortMessage.) (.setMessage ShortMessage/NOTE_ON 0 60 93)))
(def *note-off* (doto (ShortMessage.) (.setMessage ShortMessage/NOTE_OFF 0 60 93)))

(defn add-spp-events []
  (doseq [beats (take 17 (iterate inc 0))]
    (.add *track* (MidiEvent. (doto (ShortMessage.)
                                (.setMessage ShortMessage/SONG_POSITION_POINTER 0 beats))
                              (* beats 240)))))

(defn add-note-on-events []
  (doseq [ticks (take 16 (iterate #(+ 240 %) 0))]
    (.add *track* (MidiEvent. *note-on* ticks))))

(defn add-note-off-events []
  (doseq [ticks (take 17 (iterate #(+ 240 %) 240))]
    (.add *track* (MidiEvent. *note-off* ticks))))


(defn initialize-pattern []
  (add-spp-events)
  (add-note-on-events)
  (add-note-off-events))

(defn loop-bar []
  (doto *sequencer*
    (.setLoopStartPoint 0)
    (.setLoopEndPoint 3840)
    (.setLoopCount Sequencer/LOOP_CONTINUOUSLY)))

(defn clear-pattern []
  (doseq [event-num (reverse (range (.size *track*)))]
    (.remove *track* (.get *track* event-num))))


(comment
  (.open *sequencer*)
  (.start *sequencer*)
  (.stop *sequencer*)
  (.setTickPosition *sequencer* 0 )
  (.getTickPosition *sequencer*)
  (.size *track*)
  (clear-pattern)
  (loop-bar)
  (map #(.getTick  (.get *track* %1)) (range (.size *track*) ))
  (map #(.getMessage  (.get *track* %1)) (range (.size *track*) ))
  (map #(last  ( .getMessage  (.getMessage  (.get *track* %1)))) (range (.size *track*) ))

  (.remove *track*(.get *track* (- (.size *track*) 1)))

  )




