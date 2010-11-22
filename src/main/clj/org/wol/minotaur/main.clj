(ns org.wol.minotaur.main
  (:require
   [org.wol.minotaur.sequencer :as sequencer]
   [org.wol.minotaur.callbacks :as callbacks]
   [org.wol.minotaur.gui :as gui]
   [org.wol.minotaur.audio :as audio]
   [clojure.contrib.pprint :as pp])
  (:use [wol-utils.core :only [obtain-resource-url]])
  (:gen-class))

(defn init-sequencer []
  (.setSequence sequencer/*sequencer* sequencer/*sequence*)
  (.setReceiver (first (.getTransmitters sequencer/*sequencer*)) sequencer/midi-receive-handler)
  (sequencer/initialize-pattern)
  (sequencer/loop-bar)
  (reset! sequencer/*midi-receive-spp-fn* callbacks/update-step-display)
  (.open sequencer/*sequencer*))

(defn remove-gui-callbacks []
  (doseq [row (keys @gui/*checkboxes*)]
    (doseq [checkbox (get @gui/*checkboxes* row)]
      (.removeItemListener checkbox
                           (first (.getItemListeners checkbox)))))
  (.removeActionListener gui/*play-button*
                         (first (.getActionListeners gui/*stop-button*)))
  (.removeActionListener gui/*stop-button*
                         (first (.getActionListeners gui/*stop-button*)))
  (.removeChangeListener gui/*tempo-knob*
                         (first (.getChangeListeners gui/*tempo-knob*))))


(comment
  (first (.getItemListeners (first (get @gui/*checkboxes* 1))))
  (first (.getActionListeners gui/*stop-button*))


  )


(defn associate-gui-callbacks []
  (let [listener (gui/make-checkbox-listener callbacks/step-selected)]
    (doseq [i (keys @gui/*checkboxes*)]
      (doseq [checkbox (get @gui/*checkboxes* i)]
        (.addItemListener checkbox listener))))

  (.addActionListener gui/*play-button* (gui/make-button-listener callbacks/play-sequencer))
  (.addActionListener gui/*stop-button* (gui/make-button-listener callbacks/stop-sequencer))
  (.addChangeListener gui/*tempo-knob* (gui/make-knob-listener callbacks/change-tempo)))

(defn -main [& args]
  (init-sequencer)
  (gui/construct-gui)
  (associate-gui-callbacks)
  (audio/load-default-sounds))


(comment

  (-main)
  (init-sequencer)
  (gui/construct-gui)
  (associate-gui-callbacks)
  (remove-gui-callbacks)
  (audio/load-default-sounds)


  (audio/load-default-sounds)



)