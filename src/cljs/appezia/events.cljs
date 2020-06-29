(ns appezia.events
  (:require
   [re-frame.core :as re-frame :refer [reg-fx]]
   [goog.events :as events]
   [appezia.db :as db]
   ))

(def listeners (atom {}))

(defn- setup-listener! [event-type callback id]
  (let [key (events/listen js/window event-type (if (fn? callback)
                                                  callback
                                                  #(re-frame/dispatch callback)))]
    (when id
      (swap! listeners assoc-in [event-type id] key))))


(defn- stop-listener! [event-type id]
  (when-let [key (get-in @listeners [event-type id])]
    (events/unlistenByKey key)
    (swap! listeners update event-type #(dissoc % id))))


(defn on-resize [{:keys [:dispatch :debounce-ms]} timer]
  (js/clearTimeout @timer)
  (reset! timer (js/setTimeout #(re-frame/dispatch (into [] (concat dispatch [js/window.innerWidth js/window.innerHeight])))
                               (or debounce-ms 66))))

(defn on-scroll [{:keys [:dispatch :debounce-ms]} timer]
  (js/clearTimeout @timer)
  (reset! timer (js/setTimeout #(re-frame/dispatch (into [] (concat dispatch [js/window.pageYOffset])))
                               (or debounce-ms 66))))

(reg-fx
 :window/on-resize
 (fn [{:keys [:id] :as config}]
   (let [timer (atom nil)]
     (setup-listener! events/EventType.RESIZE (partial on-resize config timer) id))))

(reg-fx
 :window/on-scroll
 (fn [{:keys [:id] :as config}]
   (let [timer (atom nil)]
     (setup-listener! events/EventType.SCROLL (partial on-scroll config timer) id))))

(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))

(re-frame/reg-event-db
 ::window-resized
 (fn [db [_ w h]]
   (assoc db :width w :height h)))

(re-frame/reg-event-db
 ::scroll
 (fn [db [_ scroll-top]]
   ;(js/alert scroll-top)
   (assoc db :scroll-top scroll-top)))


(re-frame/reg-event-fx
 ::setup-resize-listener
 (fn []
   {:window/on-resize {:dispatch [::window-resized]
                       :debounce-ms 70
                        ;; :id is optional
                       :id ::resize-listener}}))

(re-frame/reg-event-fx
 ::setup-scroll-listener
 (fn []
   {:window/on-scroll {:dispatch [::scroll]
                       :debounce-ms 5
                        ;; :id is optional
                       :id ::scroll-listener}}))