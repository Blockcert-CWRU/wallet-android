//package com.learningmachine.android.app.ui.share;
//
//public class QRScannerFragment extends Fragmen {
//    private CodeScanner mCodeScanner;
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
//                             @Nullable Bundle savedInstanceState) {
//        final Activity activity = getActivity();
//        View root = inflater.inflate(R.layout.fragment_main, container, false);
//        CodeScannerView scannerView = root.findViewById(R.id.scanner_view);
//        mCodeScanner = new CodeScanner(activity, scannerView);
//        mCodeScanner.setDecodeCallback(new DecodeCallback() {
//            @Override
//            public void onDecoded(@NonNull final Result result) {
//                activity.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(activity, result.getText(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//        });
//        scannerView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mCodeScanner.startPreview();
//            }
//        });
//        return root;
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        mCodeScanner.startPreview();
//    }
//
//    @Override
//    public void onPause() {
//        mCodeScanner.releaseResources();
//        super.onPause();
//    }
//}