(ns appezia.subs
  (:require
   [re-frame.core :as re-frame :refer [reg-sub]]))

(re-frame/reg-sub
 ::name
 (fn [db]
   (:name db)))

(reg-sub
 :get
 (fn [db [_ & path]]
   (get-in db path)))