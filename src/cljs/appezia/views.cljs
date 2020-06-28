(ns appezia.views
  (:require
   [re-frame.core :as re-frame]
   [clojure.string :as str]
   [appezia.subs :as subs]))

(defn viewport-size []
  {:width js/document.documentElement.clientWidth
   :height js/document.documentElement.clientHeight})

(defn hsl
  ([hue sat lightness]
   (hsl hue sat lightness 1))
  ([hue sat lightness alpha]
   (str "hsla(" (mod hue 360) ", " sat "%, " lightness "%, " alpha ")")))

(def M "M")
(def L "L")
(def H "H")
(def V "V")
(def C "C")
(def Z "Z")

(defn trapezium [hue sat li height
                 start-top-x start-bottom-x top-length bottom-length]
  [:path {;:transform "translate(70,0)"
          :style {:fill (hsl hue sat li)}
          :d (apply str (interpose " " [M start-top-x 0
                                        L (+ start-top-x top-length 3) 0
                                        L (+ start-bottom-x bottom-length 3) height
                                        L start-bottom-x height
                                        Z]))
          #_"M150 0 L75 200 L225 200 Z"}]
  )


(defn trapezium-strip [base-hue sat li]
  
  [:div
   [:svg {:height 60
          :style {:margin-top -4}
          :width "100%"}
    [:defs [:symbol {:id (str "strip" base-hue)}
            (loop [i 87
                   hue 0
                   top -8
                   bottom -8
                   shapes [:g]]
              (if (= 0 i)
                shapes
                (let [t (* 3 (+ 1 5 #_(rand-int 14)))
                      b (* 3 (+ 1 4 #_(rand-int 14)))]
                  (recur (dec i)
                         (+ hue 71)
                         (+ t top) (+ b bottom) (conj shapes [trapezium (+ base-hue (mod hue 100) i)
                                                              sat li
                                                              60
                                                              top
                                                              bottom
                                                              t b])))))]]
    [:use {:href (str "#strip" base-hue)
           :x 0 :y 0
           :transform (str "translate(" (/ @(re-frame/subscribe [:get :width]) 2) ",0) scale(" (* 1.0 (/ (+ 213 @(re-frame/subscribe [:get :scroll-top])) 200.9)) ",1)")
           }]
    [:use {:href (str "#strip" base-hue)
           :x 0 :y 0
           :transform (str "translate(" (/ @(re-frame/subscribe [:get :width]) 2) ",0) scale(" (* -1.0 (/ (+ 213 @(re-frame/subscribe [:get :scroll-top])) 200.9))"1,1)")
           }]
    #_[trapezium 100 200 0 100 200 300]]])

(defn main-panel []
  (let [name (re-frame/subscribe [::subs/name])]
    [:div ;{:style {:background-color (hsl 180 40 94)}}
     [:div {:style {:position "fixed"
                    :top 0
                    :left 0
                    :z-index -100
                    :width "100%"
                    :height "100%"}}
      [trapezium-strip 11 73 80]
      [trapezium-strip 33 65 84]
      [trapezium-strip 66 60 88]
      [trapezium-strip 77 55 92]
      [trapezium-strip 99 50 94]
      [trapezium-strip 140 45 96]
      [trapezium-strip 180 40 97]
      
      ]
     [:div.content ;{:style {:z-index 100}}
      [:div.logo {:style {:text-align "center"}} 
       [:img {:width 420
              :src "logo.png"}]]
      #_#_[:h1.logo "appezia"]
      [:h2.logo {:style {:position "relative"
                         :top -10
                         :left 86
                         :padding-bottom 200}}
       "technologies"]
      [:p "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut lectus mauris, dictum non massa quis, condimentum pellentesque nisl. Nulla sed nisi urna. Aliquam nec elementum sem. Duis quis purus ac lectus venenatis vulputate id nec elit. Quisque vel massa ut quam accumsan fermentum. Fusce pulvinar consectetur lacinia. Morbi venenatis ullamcorper augue sed cursus. Praesent quis rhoncus eros. Sed rhoncus odio elit, in consequat turpis efficitur et. Sed pretium lorem tincidunt, congue sapien in, imperdiet erat. Nullam rhoncus nibh lectus, ut im"]
      [:p "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut lectus mauris, dictum non massa quis, condimentum pellentesque nisl. Nulla sed nisi urna. Aliquam nec elementum sem. Duis quis purus ac lectus venenatis vulputate id nec elit. Quisque vel massa ut quam accumsan fermentum. Fusce pulvinar consectetur lacinia. Morbi venenatis ullamcorper augue sed cursus. Praesent quis rhoncus eros. Sed rhoncus odio elit, in consequat turpis efficitur et. Sed pretium lorem tincidunt, congue sapien in, imperdiet erat. Nullam rhoncus nibh lectus, ut im"]
      [:p "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut lectus mauris, dictum non massa quis, condimentum pellentesque nisl. Nulla sed nisi urna. Aliquam nec elementum sem. Duis quis purus ac lectus venenatis vulputate id nec elit. Quisque vel massa ut quam accumsan fermentum. Fusce pulvinar consectetur lacinia. Morbi venenatis ullamcorper augue sed cursus. Praesent quis rhoncus eros. Sed rhoncus odio elit, in consequat turpis efficitur et. Sed pretium lorem tincidunt, congue sapien in, imperdiet erat. Nullam rhoncus nibh lectus, ut im"]]
     ]))
