import { Navigate, Route, Routes } from "react-router-dom";
import { AuthProvider } from "./context/AuthContext";
import { ProtectedRoute } from "./components/ProtectedRoute";
import { AppLayout } from "./components/AppLayout";
import { LoginPage } from "./pages/LoginPage";
import { RegisterPage } from "./pages/RegisterPage";
import { DebtsPage } from "./pages/DebtsPage";
import { PayoffComparePage } from "./pages/PayoffComparePage";

function App() {
  return (
    <AuthProvider>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route
          element={
            <ProtectedRoute>
              <AppLayout />
            </ProtectedRoute>
          }
        >
          <Route path="/debts" element={<DebtsPage />} />
          <Route path="/payoff/compare" element={<PayoffComparePage />} />
        </Route>
        <Route path="*" element={<Navigate to="/debts" replace />} />
      </Routes>
    </AuthProvider>
  );
}

export default App;
