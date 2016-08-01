
// Wart remover: find bad patterns in scala code

wartremoverWarnings in(Compile, compile) ++= Warts.unsafe
wartremoverWarnings in(Compile, compile) ++= Seq(
  PlayWart.AssetsObject,
  PlayWart.CookiesPartial,
  PlayWart.FlashPartial,
  PlayWart.FormPartial,
  PlayWart.HeadersPartial,
  PlayWart.JavaApi,
  PlayWart.JsLookupResultPartial,
  PlayWart.JsReadablePartial,
  PlayWart.LangObject,
  PlayWart.MessagesObject,
  PlayWart.PlayGlobalExecutionContext,
  PlayWart.SessionPartial,
  PlayWart.WSResponsePartial)

// Bonus Warts
wartremoverWarnings in(Compile, compile) ++= Seq(
  PlayWart.DateFormatPartial,
  PlayWart.FutureObject,
  PlayWart.GenMapLikePartial,
  PlayWart.GenTraversableLikeOps,
  PlayWart.GenTraversableOnceOps,
  PlayWart.ScalaGlobalExecutionContext,
  PlayWart.StringOpsPartial)
