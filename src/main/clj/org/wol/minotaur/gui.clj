(ns org.wol.minotaur.gui
  (:require [wol-utils.core  :as wol-utils])
  (:import [java.io ByteArrayInputStream]
           [javax.swing JFrame JPanel JCheckBox JLabel JButton ImageIcon JSlider]
           [java.awt GridLayout]
           [java.awt FlowLayout]
           [java.awt.event ItemListener ActionListener]
           [javax.swing.event ChangeListener]
           [java.awt Image]))

;;; sequencer panel
(def *sequencer-panel*
  (doto (JPanel.)
    (.setLayout (java.awt.GridLayout. 5 16))))

;;;play button
(def *play-button*
     (JButton. "Play"))

;;;stop button
(def *stop-button*
     (JButton. "Stop"))

;;;tempo knob
(def *tempo-knob*
     (JSlider. JSlider/HORIZONTAL 1 300 120))


;;;control panel
(def *control-panel*
  (doto (JPanel.)
    (.setLayout (java.awt.FlowLayout.))
    (.add *play-button*)
    (.add *stop-button*)
    (.add *tempo-knob*)))

;;;main panel
(def *main-panel*
  (doto (JPanel.)
    (.setLayout (java.awt.GridLayout. 2 1))
    (.add *sequencer-panel*)
    (.add *control-panel*)))

;;; main window
(def *frame*
  (doto (JFrame. "Minotaur")
    (.setSize 400 400)
    (.setContentPane *main-panel*)))

;;;sequencer checkboxes
(def *checkboxes* (atom  { 0 [], 1 [], 2 [], 3 [] }))

;;;sequencer step icons
(def *step-icons* (atom []))

(defn clear-main-panel []
  (.removeAll *control-panel*)
  (.removeAll *sequencer-panel*)
  (.removeAll *main-panel*)
  (.repaint *main-panel*))

(defn clear-checkboxes-from-atom []
  (reset! *checkboxes* { 0 [], 1 [], 2 [], 3 [] }))

(defn clear-step-icons-from-atom []
  (reset! *step-icons* []))

(defn add-checkboxes-to-sequencer-panel []
  (doseq [i (keys @*checkboxes*)]
    (doseq [checkbox (get @*checkboxes* i)]
      (.add *sequencer-panel* checkbox))))

(defn add-checkbox-to-atom [row nascent-button]
  (reset! *checkboxes* (merge @*checkboxes* { row (conj (get @*checkboxes* row) nascent-button)
                                   })))

(defn add-icons-to-sequencer-panel []
  (doseq [icon @*step-icons*]
    (.add *sequencer-panel* icon)))

(defn add-icons-to-atom [icon]
  (println icon)
  (swap! *step-icons* conj icon ))

(comment
  (defmulti update-pattern (fn [state checkbox-tuple event] (.getStateChange event)))
  (defmethod update-pattern java.awt.event.ItemEvent/SELECTED [state checkbox-tuple _]
    (let [row (first checkbox-tuple)
          step (second checkbox-tuple)]
      (do
        (prn (format "row: %s step: %s" row step))
        (merge state { row (assoc (get state row) step 1) }))))

  (defmethod update-pattern java.awt.event.ItemEvent/DESELECTED [state checkbox-tuple _]
    (let [row (first checkbox-tuple)
          step (second checkbox-tuple)]
      (merge state { row (assoc (get state row) step 0) }))))


(defn create-checkbox [row col]
  (add-checkbox-to-atom row (doto (JCheckBox. (format "%d-%d" row col )))))


(defn create-checkboxes []
  (doseq [i (keys @*checkboxes*)]
    (dotimes [j 16]
      (create-checkbox i j))))

;;; (ClassLoader/getSystemResource  "Button Next.png")
(defn create-icons []
  (let [imgUrl    (wol-utils/obtain-resource-url *ns* "Button Next.png")
        icon      (ImageIcon. imgUrl "awesome button")
        image     (.getImage icon)
        new-image (.getScaledInstance image 20 20 Image/SCALE_SMOOTH)
        new-icon  (ImageIcon. new-image)]
    (dotimes [_ 16]
      (println imgUrl)
      (add-icons-to-atom (doto  (JLabel. new-icon) (.setVisible false))))))


(comment
  (.getResourceAsStream  (.getClass *ns*) "Button Next.png")
  (ClassLoader/getSystemResource "Button Next.png")

  (-> (.getClass *ns*) (.getClassLoader) (.findResource "Button Next.png"))
  (ClassLoader/getSystemClassLoader)

  )

(defn make-checkbox-listener [fn]
  (proxy [ItemListener] []
    (itemStateChanged [e]
                      (fn e))))

(defn make-button-listener [fn]
  (proxy [ActionListener] []
    (actionPerformed [e]
                     (fn))))

(defn make-knob-listener [fn]
  (proxy [ChangeListener] []
      (stateChanged [e]
                    (fn e))))

(defn construct-gui []
  (create-icons)
  (add-icons-to-sequencer-panel)
  (create-checkboxes)
  (add-checkboxes-to-sequencer-panel)
  (.setVisible *frame* true))

(comment
  (clear-checkboxes-from-atom)
  (clear-step-icons-from-atom)

  (clear-main-panel)
  (.setVisible *frame* false)

  (.readLine  (java.io.BufferedReader.  (java.io.FileReader. (.getFile (wol-utils/obtain-resource *ns* "application.properties")))))
  )




